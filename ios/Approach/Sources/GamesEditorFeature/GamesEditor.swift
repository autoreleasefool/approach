import ComposableArchitecture
import FeatureActionLibrary
import FramesDataProviderInterface
import SharedModelsLibrary

public struct GamesEditor: ReducerProtocol {
	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler>
		public var bowlerGames: [Bowler.ID: [Game.ID]]

		public var currentBowler: Bowler.ID
		public var currentGame: Game.ID

		public var currentFrame = 1
		public var currentBall = 1

		public var sheet: SheetState = .presenting(.gameDetails)

		public init(
			bowlers: IdentifiedArrayOf<Bowler>,
			bowlerGames: [Bowler.ID: [Game.ID]],
			currentBowler: Bowler.ID,
			currentGame: Game.ID
		) {
			precondition(bowlers[id: currentBowler] != nil)
			precondition(bowlerGames[currentBowler]?.contains(currentGame) == true)
			precondition(bowlerGames.allSatisfy { $0.value.count == bowlerGames.first!.value.count })
			self.bowlers = bowlers
			self.bowlerGames = bowlerGames
			self.currentBowler = currentBowler
			self.currentGame = currentGame
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case setGamePicker(isPresented: Bool)
			case setGameDetails(isPresented: Bool)
			case didDismissOpenSheet
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case switchToBowler(Bowler.ID)
			case switchToGame(Game.ID)
			case framesResponse(TaskResult<[Frame]>)

			case ballDetails(BallDetails.Action)
			case gameIndicator(GameIndicator.Action)
			case gamePicker(GamePicker.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	struct CancelObservationID {}

	public init() {}

	@Dependency(\.framesDataProvider) var framesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.ballDetails, action: /Action.internal..Action.InternalAction.ballDetails) {
			BallDetails()
		}

		Scope(state: \.gameIndicator, action: /Action.internal..Action.InternalAction.gameIndicator) {
			GameIndicator()
		}

		Scope(state: \.gamePicker, action: /Action.internal..Action.InternalAction.gamePicker) {
			GamePicker()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return loadGameDetails(for: state.currentGame)

				case .didDismissOpenSheet:
					state.sheet.finishTransition()
					return .none

				case let .setGameDetails(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .gameDetails)
					return .none

				case let .setGamePicker(isPresented):
					state.sheet.handle(isPresented: isPresented, for: .gamePicker)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .switchToBowler(bowlerId):
					let gameIndex = state.bowlerGames[state.currentBowler]!.firstIndex(of: state.currentGame)!
					state.currentBowler = bowlerId
					state.currentGame = state.bowlerGames[bowlerId]![gameIndex]
					return loadGameDetails(for: state.currentGame)

				case let .switchToGame(gameId):
					precondition(state.bowlerGames[state.currentBowler]!.contains(gameId))
					state.currentGame = gameId
					return loadGameDetails(for: state.currentGame)

				case let .framesResponse(.success(frames)):
					guard frames.first?.game == state.currentGame else {
						// TODO: log error that unexpected frames loaded (should be cancelled in flight)
						return .none
					}

					return .none

				case .framesResponse(.failure):
					// TODO: handle error loading frames
					return .none

				case let .gamePicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinish:
						state.sheet.hide(.gamePicker)
						return .none
					}

				case let .ballDetails(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .gameIndicator(.delegate(delegateAction)):
					switch delegateAction {
					case .didRequestGamePicker:
						state.sheet.transition(to: .gamePicker)
						return .none
					}

				case .gamePicker(.view), .gamePicker(.internal):
					return .none

				case .gameIndicator(.view), .gameIndicator(.internal):
					return .none

				case .ballDetails(.view), .ballDetails(.internal), .ballDetails(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func loadGameDetails(for gameId: Game.ID) -> EffectTask<Action> {
		return .task {
			await .internal(.framesResponse(TaskResult {
				try await framesDataProvider.fetchFrames(.init(filter: .game(gameId), ordering: .byOrdinal))
			}))
		}
		.cancellable(id: CancelObservationID.self, cancelInFlight: true)
	}
}

extension GamesEditor.State {
	var ballDetails: BallDetails.State {
		get { .init(frame: currentFrame, ball: currentBall) }
		set {
			self.currentFrame = newValue.frame
			self.currentBall = newValue.ball
		}
	}
}

extension GamesEditor.State {
	var currentBowlerGames: [Game.ID] {
		bowlerGames[currentBowler] ?? []
	}

	var gameIndicator: GameIndicator.State {
		get { .init(games: currentBowlerGames, selected: currentGame) }
		set { self.currentGame = newValue.selected }
	}

	var gamePicker: GamePicker.State {
		get { .init(games: currentBowlerGames, selected: currentGame) }
		set { self.currentGame = newValue.selected }
	}
}

extension GamesEditor.State {
	public enum Sheet: Equatable {
		case gamePicker
		case gameDetails

		static let `default`: Self = .gameDetails
	}

	public enum SheetState: Equatable {
		case presenting(Sheet)
		case transitioning(to: Sheet)

		mutating func handle(isPresented: Bool, for sheet: Sheet) {
			switch self {
			case .presenting:
				if isPresented {
					self.transition(to: sheet)
				} else {
					self.transition(to: .default)
				}
			case .transitioning:
				break
			}
		}

		mutating func transition(to: Sheet) {
			switch self {
			case .presenting(to):
				break
			case .presenting, .transitioning:
				self = .transitioning(to: to)
			}
		}

		mutating func hide(_ sheet: Sheet) {
			switch self {
			case .presenting(sheet), .transitioning(to: sheet):
				self.transition(to: .default)
			case .presenting, .transitioning:
				break
			}
		}

		mutating func finishTransition() {
			switch self {
			case let .transitioning(to):
				self = .presenting(to)
			case .presenting:
				break
			}
		}
	}
}

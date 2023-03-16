import ComposableArchitecture
import FeatureActionLibrary
import FramesDataProviderInterface
import SharedModelsLibrary
import SwiftUI

public struct GamesEditor: ReducerProtocol {
	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler>
		public var bowlerGames: [Bowler.ID: [Game.ID]]

		public var currentBowler: Bowler.ID
		public var currentGame: Game.ID

		public var frames: [MutableFrame]?
		public var frameIndex = 0
		public var rollIndex = 0
		public var draggedPinNewState: Bool?

		@BindableState public var detent: PresentationDetent = .height(.zero)
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

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didMeasureSheetHeight(CGFloat)
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
			case frameEditor(FrameEditor.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	struct CancelObservationID {}

	public init() {}

	@Dependency(\.framesDataProvider) var framesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

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

				case let .didMeasureSheetHeight(newHeight):
					if state.detent == .height(.zero) {
						state.detent = .height(newHeight)
					}
					return .none

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

					state.frames = frames.map { .init(from: $0) }
					state.frames?[state.frameIndex].guaranteeRollExists(upTo: state.rollIndex)
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

				case let .frameEditor(.delegate(delegateAction)):
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

				case .frameEditor(.view), .frameEditor(.internal):
					return .none

				case .gamePicker(.view), .gamePicker(.internal):
					return .none

				case .gameIndicator(.view), .gameIndicator(.internal):
					return .none

				case .ballDetails(.view), .ballDetails(.internal), .ballDetails(.binding):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.frameEditor, action: /Action.internal..Action.InternalAction.frameEditor) {
			FrameEditor()
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
		// TODO: replace with frameIndex
		get { .init(frame: frameIndex + 1, ball: rollIndex + 1) }
		set {
			self.rollIndex = newValue.ball - 1
			self.frameIndex = newValue.frame - 1
			self.frames?[self.frameIndex].guaranteeRollExists(upTo: newValue.ball - 1)
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
	var frameEditor: FrameEditor.State? {
		get {
			guard let frames else { return nil }
			return .init(
				rollIndex: rollIndex,
				frame: frames[frameIndex],
				draggedPinNewState: draggedPinNewState
			)
		}
		set {
			guard let newValue else { return }
			self.rollIndex = newValue.rollIndex
			self.frames?[self.frameIndex] = newValue.frame
			self.frames?[self.frameIndex].guaranteeRollExists(upTo: self.rollIndex)
			self.draggedPinNewState = newValue.draggedPinNewState
		}
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

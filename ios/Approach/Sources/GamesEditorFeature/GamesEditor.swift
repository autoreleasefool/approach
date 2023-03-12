import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct GamesEditor: ReducerProtocol {
	public struct State: Equatable {
		public var games: IdentifiedArrayOf<Game>

		public var currentGame: Game.ID
		public var currentFrame: Int
		public var currentBall: Int

		public var sheet: SheetState = .presenting(.gameDetails)

		public var game: Game { games[id: currentGame]! }

		public init(games: IdentifiedArrayOf<Game>, current: Game.ID) {
			precondition(games[id: current] != nil)

			self.games = games
			self.currentGame = current
			self.currentFrame = 1
			self.currentBall = 1
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setGamePicker(isPresented: Bool)
			case setGameDetails(isPresented: Bool)
			case didDismissOpenSheet
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case ballDetails(BallDetails.Action)
			case gameIndicator(GameIndicator.Action)
			case gamePicker(GamePicker.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

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
	var gameIndicator: GameIndicator.State {
		get { .init(games: games, selected: currentGame) }
		set { self.currentGame = newValue.selected }
	}
}

extension GamesEditor.State {
	var gamePicker: GamePicker.State {
		get { .init(games: games, selected: currentGame) }
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

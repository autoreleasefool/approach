import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct GamesEditor: ReducerProtocol {
	public struct State: Equatable {
		public var games: IdentifiedArrayOf<Game>
		public var selectedGame: Game.ID

		public var currentFrame: Int
		public var currentBall: Int

		public var game: Game { games[id: selectedGame]! }

		public init(games: IdentifiedArrayOf<Game>, selected: Game.ID) {
			precondition(games[id: selected] != nil)

			self.games = games
			self.selectedGame = selected
			self.currentFrame = 1
			self.currentBall = 1
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case ballDetails(BallDetails.Action)
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

		Scope(state: \.gamePicker, action: /Action.internal..Action.InternalAction.gamePicker) {
			GamePicker()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .ballDetails(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .gamePicker(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .gamePicker(.view), .gamePicker(.internal):
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
		get {
			.init(frame: currentFrame, ball: currentBall)
		}
		set {
			self.currentFrame = newValue.frame
			self.currentBall = newValue.ball
		}
	}
}

extension GamesEditor.State {
	var gamePicker: GamePicker.State {
		get {
			.init(games: games, selected: selectedGame)
		}
		set {
			self.selectedGame = newValue.selected
		}
	}
}

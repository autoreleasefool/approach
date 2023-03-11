import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct GamePicker: ReducerProtocol {
	public struct State: Equatable {
		public var games: IdentifiedArrayOf<Game>
		public var selected: Game.ID

		public init(games: IdentifiedArrayOf<Game>, selected: Game.ID) {
			self.games = games
			self.selected = selected
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapGame(Game.ID)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapGame(id):
					state.selected = id
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

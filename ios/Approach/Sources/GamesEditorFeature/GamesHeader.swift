import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary

public struct GamesHeader: Reducer {
	public struct State: Equatable {
		public let numberOfGames: Int
		public let currentGameOrdinal: Int

		init(numberOfGames: Int, currentGameOrdinal: Int) {
			self.numberOfGames = numberOfGames
			self.currentGameOrdinal = currentGameOrdinal
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didCloseEditor
			case didOpenSettings
			case didOpenGamePicker
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
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

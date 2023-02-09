import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct LaneEditor: ReducerProtocol {
	public struct State: Identifiable, Equatable {
		public let id: Lane.ID
		@BindableState public var label: String
		@BindableState public var isAgainstWall: Bool

		public init(id: Lane.ID, label: String = "", isAgainstWall: Bool = false) {
			self.id = id
			self.label = label
			self.isAgainstWall = isAgainstWall
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didSwipe(SwipeAction)
		}
		public enum DelegateAction: Equatable {
			case didSwipe(SwipeAction)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public enum SwipeAction: Equatable {
		case delete
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didSwipe(action):
					switch action {
					case .delete:
						return .task { .delegate(.didSwipe(action)) }
					}
				}

			case .binding, .internal, .delegate:
				return .none
			}
		}
	}
}

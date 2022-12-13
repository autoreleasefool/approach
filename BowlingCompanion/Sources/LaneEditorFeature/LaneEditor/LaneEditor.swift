import ComposableArchitecture
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

	public enum Action: BindableAction, Equatable {
		case swipeAction(SwipeAction)
		case binding(BindingAction<State>)
	}

	public enum SwipeAction: Equatable {
		case delete
	}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { _, action in
			switch action {
			case .binding, .swipeAction:
				return .none
			}
		}
	}
}

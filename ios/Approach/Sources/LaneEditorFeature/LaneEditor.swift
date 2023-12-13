import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary

extension Lane.Position: CustomStringConvertible {
	public var description: String {
		switch self {
		case .leftWall: return Strings.Lane.Properties.Position.leftWall
		case .rightWall: return Strings.Lane.Properties.Position.rightWall
		case .noWall: return Strings.Lane.Properties.Position.noWall
		}
	}
}

@Reducer
public struct LaneEditor: Reducer {
	public struct State: Identifiable, Equatable {
		public let id: Lane.ID
		@BindingState public var label: String
		@BindingState public var position: Lane.Position

		public init(id: Lane.ID, label: String, position: Lane.Position) {
			self.id = id
			self.label = label
			self.position = position
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction: BindableAction {
			case didSwipe(SwipeAction)
			case binding(BindingAction<State>)
		}
		@CasePathable public enum DelegateAction {
			case didDeleteLane
		}
		@CasePathable public enum InternalAction { case doNothing }

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum SwipeAction {
		case delete
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didSwipe(action):
					switch action {
					case .delete:
						return .send(.delegate(.didDeleteLane))
					}

				case .binding:
					return .none
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

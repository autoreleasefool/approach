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
public struct LaneEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Identifiable, Equatable {
		public let id: Lane.ID
		public var label: String
		public var position: Lane.Position

		public init(id: Lane.ID, label: String, position: Lane.Position) {
			self.id = id
			self.label = label
			self.position = position
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didSwipe(SwipeAction)
		}
		@CasePathable public enum Delegate {
			case didDeleteLane
		}
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum SwipeAction {
		case delete
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didSwipe(action):
					switch action {
					case .delete:
						return .send(.delegate(.didDeleteLane))
					}
				}

			case .internal(.doNothing):
				return .none

			case .delegate, .binding:
				return .none
			}
		}
	}
}

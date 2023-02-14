import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct BallDetails: ReducerProtocol {
	public struct State: Equatable {
		public var frame: Int
		public var ball: Int
		@BindableState public var fouled: Bool = false
		@BindableState public var ballRolled: Gear?

		init(frame: Int, ball: Int) {
			self.frame = frame
			self.ball = ball
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapNextButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)

	}

	init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapNextButton:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

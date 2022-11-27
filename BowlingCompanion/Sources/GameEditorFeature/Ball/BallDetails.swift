import ComposableArchitecture
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

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case nextButtonTapped
	}

	init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { _, action in
			switch action {
			case .binding, .nextButtonTapped:
				return .none
			}
		}
	}
}

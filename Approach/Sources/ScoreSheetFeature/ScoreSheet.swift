import ComposableArchitecture
import SharedModelsLibrary

public struct ScoreSheet: ReducerProtocol {
	public struct State: Equatable {
		public var frames: IdentifiedArrayOf<Frame> = []
		public var selection: Frame.ID?

		public init() {}
	}

	public enum Action: Equatable {
		case setFrame(id: Frame.ID?)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .setFrame(.some(id)):
				state.selection = id
				return .none

			case .setFrame(.none):
				state.selection = nil
				return .none
			}
		}
	}
}

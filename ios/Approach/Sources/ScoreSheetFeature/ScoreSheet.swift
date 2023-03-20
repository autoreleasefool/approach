import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct ScoreSheet: Reducer {
	public struct State: Equatable {
		public let frames: [Frame]
		public var currentFrameIndex: Int
		public var currentRollIndex: Int

		public init(
			frames: [Frame],
			currentFrameIndex: Int,
			currentRollIndex: Int
		) {
			self.frames = frames
			self.currentFrameIndex = currentFrameIndex
			self.currentRollIndex = currentRollIndex
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapFrame(index: Int, rollIndex: Int?)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapFrame(frameIndex, rollIndex):
					state.currentFrameIndex = frameIndex
					if let rollIndex {
						state.currentRollIndex = rollIndex
					} else {
						state.currentRollIndex = state.frames[state.currentFrameIndex].rolls.count - 1
					}

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

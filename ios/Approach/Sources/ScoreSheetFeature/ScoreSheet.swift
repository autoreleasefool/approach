import ComposableArchitecture
import FeatureActionLibrary
import FramesRepositoryInterface
import ModelsLibrary

public struct ScoreSheet: Reducer {
	public struct State: Equatable {
		public let data: DataSource
		public var currentFrameIndex: Int
		public var currentRollIndex: Int

		public init(data: DataSource, currentFrameIndex: Int, currentRollIndex: Int) {
			self.data = data
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

	public enum DataSource: Equatable {
		case summaries([Frame.Summary])
		case edits([Frame.Edit])
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
						switch state.data {
						case let .edits(frames):
							state.currentRollIndex = frames[state.currentFrameIndex].rolls.endIndex - 1
						case let .summaries(frames):
							state.currentRollIndex = frames[state.currentFrameIndex].rolls.endIndex - 1
						}
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

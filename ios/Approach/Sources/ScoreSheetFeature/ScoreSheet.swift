import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ScoringServiceInterface

public struct ScoreSheet: Reducer {
	public struct State: Equatable {
		public let steps: [ScoreStep]
		public var currentFrameIndex: Int
		public var currentRollIndex: Int

		public init(steps: [ScoreStep], currentFrameIndex: Int, currentRollIndex: Int) {
			self.steps = steps
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

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapFrame(frameIndex, rollIndex):
					state.currentFrameIndex = frameIndex
					if let rollIndex {
						state.currentRollIndex = state.steps[state.currentFrameIndex].lastValidIndex(upTo: rollIndex)
					} else {
						state.currentRollIndex = state.steps[state.currentFrameIndex].lastValidIndex(upTo: Frame.NUMBER_OF_ROLLS - 1)
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

extension ScoreStep {
	func lastValidIndex(upTo rollIndex: Int) -> Int {
		guard rollIndex > 0, !Frame.isLast(index) else { return rollIndex }
		if rolls.first?.display == "X" {
			return 0
		} else if rolls.dropFirst().first?.display == "/" {
			return 1
		} else {
			return rolls.first(where: { $0.display?.isEmpty ?? true })?.index ?? rollIndex
		}
	}
}

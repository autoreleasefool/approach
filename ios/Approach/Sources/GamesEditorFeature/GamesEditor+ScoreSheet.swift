import ComposableArchitecture
import ScoreSheetFeature

extension GamesEditor.State {
	var scoreSheet: ScoreSheet.State? {
		get {
			guard let score else { return nil }
			return .init(
				steps: score,
				currentFrameIndex: currentFrameIndex,
				currentRollIndex: currentRollIndex
			)
		}
		set {
			guard let newValue else { return }
			setCurrent(rollIndex: newValue.currentRollIndex, frameIndex: newValue.currentFrameIndex)
			let currentFrameIndex = self.currentFrameIndex
			let currentRollIndex = self.currentRollIndex
			frames?[currentFrameIndex].guaranteeRollExists(upTo: currentRollIndex)
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, scoreSheetAction: ScoreSheet.Action) -> Effect<Action> {
		switch scoreSheetAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .never:
				return .none
			}

		case .view, .internal:
			return .none
		}
	}
}

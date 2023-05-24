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
			currentRollIndex = newValue.currentRollIndex
			currentFrameIndex = newValue.currentFrameIndex
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

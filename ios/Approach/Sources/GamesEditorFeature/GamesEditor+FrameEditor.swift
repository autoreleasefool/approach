import ComposableArchitecture
import ModelsLibrary

extension GamesEditor.State {
	mutating func syncFrameEditorSharedState() {
		guard let frames else { return }
		let frame = frames[currentFrameIndex]
		frameEditor.isEditable = isEditable
		let pinsDownLastRoll = currentRollIndex > 0 ? frame.deck(forRoll: currentRollIndex - 1) : []
		if Frame.isLast(frame.index) {
			frameEditor.lockedPins = pinsDownLastRoll.arePinsCleared ? [] : pinsDownLastRoll
		} else {
			frameEditor.lockedPins = pinsDownLastRoll
		}
		frameEditor.downedPins = frame.deck(forRoll: currentRollIndex).subtracting(frameEditor.lockedPins)
	}
}

extension GamesEditor {
	func reduce(into state: inout State, frameEditorAction: FrameEditor.Action) -> Effect<Action> {
		switch frameEditorAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didEditFrame:
				let currentFrameIndex = state.currentFrameIndex
				let currentRollIndex = state.currentRollIndex
				let downedPins = state.frameEditor.downedPins
				state.frames?[currentFrameIndex].setDownedPins(rollIndex: currentRollIndex, to: downedPins)
				state.setCurrent()
				state.syncFrameEditorSharedState()
				state.syncRollEditorSharedState()
				return save(frame: state.frames?[state.currentFrameIndex])

			case .didProvokeLock:
				return state.presentLockedAlert()
			}

		case .view, .internal:
			return .none
		}
	}
}

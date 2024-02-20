import ComposableArchitecture
import ModelsLibrary

extension GamesEditor.State {
	var frameEditor: FrameEditor.State? {
		get {
			guard let frames else { return nil }
			let frame = frames[currentFrameIndex]
			var frameEditor = _frameEditor
			frameEditor.isEditable = isEditable
			let pinsDownLastRoll = currentRollIndex > 0 ? frame.deck(forRoll: currentRollIndex - 1) : []
			if Frame.isLast(frame.index) {
				frameEditor.lockedPins = pinsDownLastRoll.arePinsCleared ? [] : pinsDownLastRoll
			} else {
				frameEditor.lockedPins = pinsDownLastRoll
			}
			frameEditor.downedPins = frame.deck(forRoll: currentRollIndex).subtracting(frameEditor.lockedPins)
			return frameEditor
		}
		set {
			guard let newValue else { return }
			_frameEditor = newValue
			let currentFrameIndex = self.currentFrameIndex
			let currentRollIndex = self.currentRollIndex
			self.frames?[currentFrameIndex].setDownedPins(rollIndex: currentRollIndex, to: newValue.downedPins)
			self.setCurrent()
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, frameEditorAction: FrameEditor.Action) -> Effect<Action> {
		switch frameEditorAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didEditFrame:
				return save(frame: state.frames?[state.currentFrameIndex])

			case .didProvokeLock:
				return state.presentLockedAlert()
			}

		case .view, .internal:
			return .none
		}
	}
}

import ComposableArchitecture

extension GamesEditor.State {
	var frameEditor: FrameEditor.State? {
		get {
			guard let _frameEditor, let frames else { return nil }
			var frameEditor = _frameEditor
			frameEditor.isEditable = isEditable
			frameEditor.currentRollIndex = currentRollIndex
			frameEditor.frame = frames[currentFrameIndex]
			return frameEditor
		}
		set {
			_frameEditor = newValue
			guard let newValue else { return }
			let currentFrameIndex = self.currentFrameIndex
			self.frames?[currentFrameIndex] = newValue.frame
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
				return state.presentLockedToast()
			}

		case .view, .internal:
			return .none
		}
	}
}

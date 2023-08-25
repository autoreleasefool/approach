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
			guard isEditable else { return }
			_frameEditor = newValue
			guard let newValue else { return }
			self.frames?[self.currentFrameIndex] = newValue.frame
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, frameEditorAction: FrameEditor.Action) -> Effect<Action> {
		switch frameEditorAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didEditFrame:
				return .merge(
					save(frame: state.frames?[state.currentFrameIndex]),
					updateScoreSheet(from: state)
				)
			}

		case .view, .internal:
			return .none
		}
	}
}

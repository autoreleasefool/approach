import ComposableArchitecture

extension GamesEditor.State {
	var rollEditor: RollEditor.State? {
		get {
			guard let _rollEditor, let frames else { return nil }
			var rollEditor = _rollEditor
			let currentRoll = frames[currentFrameIndex].rolls[currentRollIndex]
			rollEditor.ballRolled = currentRoll.bowlingBall
			rollEditor.didFoul = currentRoll.roll.didFoul
			return rollEditor
		}
		set {
			guard isEditable else { return }
			_rollEditor = newValue
			guard let newValue else { return }
			frames?[currentFrameIndex].setDidFoul(newValue.didFoul, forRoll: currentRollIndex)
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, rollEditorAction: RollEditor.Action) -> Effect<Action> {
		switch rollEditorAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didTapBall:
				state.sheet.transition(to: .ballPicker)
				return .none

			case .didEditRoll:
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

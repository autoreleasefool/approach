import ComposableArchitecture

extension GamesEditor.State {
	var ballPicker: BallPicker.State {
		get {
			var picker = _ballPicker
			picker.forBowler = currentBowlerId
			picker.selected = frames?[currentFrameIndex].rolls[currentRollIndex].bowlingBall?.id
			return picker
		}
		set {
			guard isEditable else { return }
			_ballPicker = newValue
			frames?[currentFrameIndex].setBowlingBall(newValue.selectedBall?.rolled, forRoll: currentRollIndex)
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, ballPickerAction: BallPicker.Action) -> Effect<Action> {
		switch ballPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didFinish:
				state.sheet.hide(.ballPicker)
				return save(frame: state.frames?[state.currentFrameIndex])
			}

		case .view, .internal:
			return .none
		}
	}
}

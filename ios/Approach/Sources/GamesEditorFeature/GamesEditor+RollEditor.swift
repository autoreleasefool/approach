import ComposableArchitecture

extension GamesEditor.State {
	var rollEditor: RollEditor.State? {
		get {
			guard let frames else { return nil }
			var rollEditor = _rollEditor
			let currentRoll = frames[currentFrameIndex].rolls[currentRollIndex]
			rollEditor.ballRolled = currentRoll.bowlingBall
			rollEditor.didFoul = currentRoll.roll.didFoul
			rollEditor.isEditable = isEditable
			return rollEditor
		}
		set {
			guard let newValue else { return }
			_rollEditor = newValue
			let currentFrameIndex = self.currentFrameIndex
			let currentRollIndex = self.currentRollIndex
			frames?[currentFrameIndex].setDidFoul(newValue.didFoul, forRoll: currentRollIndex)
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, rollEditorAction: RollEditor.Action) -> Effect<Action> {
		switch rollEditorAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeBall(ball):
				let currentFrameIndex = state.currentFrameIndex
				let currentRollIndex = state.currentRollIndex
				state.frames?[currentFrameIndex].setBowlingBall(ball, forRoll: currentRollIndex)
				return save(frame: state.frames?[state.currentFrameIndex])

			case .didRequestBallPicker:
				let bowlingBall = state.frames?[state.currentFrameIndex].rolls[state.currentRollIndex].bowlingBall?.id
				state.destination = .sheets(.ballPicker(.init(
					selected: Set([bowlingBall].compactMap { $0 }),
					query: .init(()),
					limit: 1
				)))
				return .none

			case .didEditRoll:
				return save(frame: state.frames?[state.currentFrameIndex])

			case .didProvokeLock:
				return state.presentLockedAlert()
			}

		case .view, .internal:
			return .none
		}
	}
}

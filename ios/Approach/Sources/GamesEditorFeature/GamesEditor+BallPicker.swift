import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		ballPickerAction: ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action
	) -> Effect<Action> {
		switch ballPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeSelection(bowlingBalls):
				let currentFrameIndex = state.currentFrameIndex
				let currentRollIndex = state.currentRollIndex
				state.frames?[currentFrameIndex].setBowlingBall(bowlingBalls.first?.named, forRoll: currentRollIndex)
				return save(frame: state.frames?[state.currentFrameIndex])
			}

		case .view, .internal:
			return .none
		}
	}
}

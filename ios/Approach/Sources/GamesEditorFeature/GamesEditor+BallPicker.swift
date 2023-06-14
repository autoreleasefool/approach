import ComposableArchitecture
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		ballPickerAction: ResourcePicker<Gear.Summary, Bowler.ID>.Action
	) -> Effect<Action> {
		switch ballPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeSelection(bowlingBalls):
				state.frames?[state.currentFrameIndex].setBowlingBall(bowlingBalls.first?.named, forRoll: state.currentRollIndex)
				return save(frame: state.frames?[state.currentFrameIndex])
			}

		case .view, .internal:
			return .none
		}
	}
}

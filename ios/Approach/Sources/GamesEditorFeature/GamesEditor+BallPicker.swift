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
				let rolledBall = bowlingBalls.first
				state.frames?[currentFrameIndex].setBowlingBall(rolledBall, forRoll: currentRollIndex)
				return .merge(
					save(frame: state.frames?[state.currentFrameIndex]),
					.run { [id = rolledBall?.id] _ in
						guard let id else { return }
						recentlyUsed.didRecentlyUseResource(.gear, id)
					}
				)
			}

		case .view, .internal:
			return .none
		}
	}
}

import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		opponentPickerAction: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action
	) -> Effect<Action> {
		switch opponentPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeSelection(opponent):
				guard state.isEditable else { return .none }
				state.game?.matchPlay?.opponent = opponent.first
				return save(matchPlay: state.game?.matchPlay)
			}

		case .view, .internal:
			return .none
		}
	}
}

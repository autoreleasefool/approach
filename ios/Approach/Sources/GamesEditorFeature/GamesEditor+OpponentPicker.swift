import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		opponentPickerAction: ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.Action
	) -> Effect<Action> {
		switch opponentPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeSelection(opponent):
				state.game?.matchPlay?.opponent = opponent.first?.summary
				return save(matchPlay: state.game?.matchPlay)
			}

		case .view, .internal:
			return .none
		}
	}
}

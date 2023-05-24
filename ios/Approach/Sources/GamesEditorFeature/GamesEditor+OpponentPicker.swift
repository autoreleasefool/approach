import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor.State {
	var opponentPicker: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State {
		get {
			var picker = _opponentPicker
			picker.initialSelection = Set([game?.matchPlay?.opponent?.id].compactMap { $0 })
			return picker
		}
		set {
			guard isEditable else { return }
			_opponentPicker = newValue
		}
	}
}

extension GamesEditor {
	func reduce(
		into state: inout State,
		opponentPickerAction: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action
	) -> Effect<Action> {
		switch opponentPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didFinishEditing:
				state.sheet.hide(.opponentPicker)
				guard state.isEditable && state.gameDetails != nil else { return .none }
				return state.gameDetails!.setMatchPlay(opponent: state._opponentPicker.selectedResources?.first)
					.map { .internal(.gameDetails($0)) }
			}

		case .view, .internal:
			return .none
		}
	}
}

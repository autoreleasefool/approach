import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor.State {
	var gearPicker: ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State {
		get {
			var picker = _gearPicker
			picker.initialSelection = Set((game?.gear ?? []).map(\.id))
			return picker
		}
		set {
			guard isEditable else { return }
			_gearPicker = newValue
		}
	}
}

extension GamesEditor {
	func reduce(
		into state: inout State,
		gearPickerAction: ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action
	) -> Effect<Action> {
		switch gearPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didFinishEditing:
				state.sheet.hide(.gearPicker)
				guard state.isEditable && state.gameDetails != nil else { return .none }
				return state.gameDetails!.setGear(state._gearPicker.selectedResources ?? [])
					.map { .internal(.gameDetails($0)) }
			}

		case .view, .internal:
			return .none
		}
	}
}

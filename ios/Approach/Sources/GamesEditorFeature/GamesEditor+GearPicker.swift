import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		gearPickerAction: ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action
	) -> Effect<Action> {
		switch gearPickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeSelection(gear):
				state.game?.gear = .init(uniqueElements: gear)
				return save(game: state.game)
			}

		case .view, .internal:
			return .none
		}
	}
}

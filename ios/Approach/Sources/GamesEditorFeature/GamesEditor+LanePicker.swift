import ComposableArchitecture
import EquatableLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		lanePickerAction: ResourcePicker<Lane.Summary, Alley.ID>.Action
	) -> Effect<Action> {
		switch lanePickerAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didChangeSelection(lanes):
				guard state.isEditable else { return .none }
				state.game?.lanes = .init(uniqueElements: lanes.map(\.asLaneInfo))
				return save(game: state.game)
			}

		case .view, .internal:
			return .none
		}
	}
}

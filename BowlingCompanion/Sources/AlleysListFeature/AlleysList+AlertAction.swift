import ComposableArchitecture
import SharedModelsLibrary

extension AlleysList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Alley)
		case dismissed
	}

	static func alert(toDelete alley: Alley) -> AlertState<AlertAction> {
		.init(
			title: TextState("Are you sure you want to delete \(alley.name)"),
			primaryButton: .destructive(
				TextState("Delete"),
				action: .send(.deleteButtonTapped(alley))
			),
			secondaryButton: .cancel(
				TextState("Cancel"),
				action: .send(.dismissed)
			)
		)
	}
}

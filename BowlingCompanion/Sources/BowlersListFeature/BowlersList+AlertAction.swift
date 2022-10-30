import ComposableArchitecture
import SharedModelsLibrary

extension BowlersList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Bowler)
		case dismissed
	}

	static func alert(toDelete bowler: Bowler) -> AlertState<AlertAction> {
		.init(
			title: TextState("Are you sure you want to delete \(bowler.name)"),
			primaryButton: .destructive(
				TextState("Delete"),
				action: .send(.deleteButtonTapped(bowler))
			),
			secondaryButton: .cancel(
				TextState("Cancel"),
				action: .send(.dismissed)
			)
		)
	}
}

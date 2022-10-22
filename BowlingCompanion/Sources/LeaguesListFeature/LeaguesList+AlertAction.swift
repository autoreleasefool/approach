import ComposableArchitecture
import SharedModelsLibrary

extension LeaguesList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(League)
		case dismissed
	}

	func alert(toDelete league: League) -> AlertState<AlertAction> {
		.init(
			title: TextState("Are you sure you want to delete \(league.name)"),
			primaryButton: .destructive(
				TextState("Delete"),
				action: .send(.deleteButtonTapped(league))
			),
			secondaryButton: .cancel(
				TextState("Cancel"),
				action: .send(.dismissed)
			)
		)
	}
}

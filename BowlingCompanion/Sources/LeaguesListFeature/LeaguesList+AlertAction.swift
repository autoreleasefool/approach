import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension LeaguesList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(League)
		case dismissed
	}

	func alert(toDelete league: League) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(league.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(league))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

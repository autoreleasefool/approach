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
			title: TextState(Strings.Leagues.List.Delete.title(league.name)),
			primaryButton: .destructive(
				TextState(Strings.Leagues.List.Delete.action),
				action: .send(.deleteButtonTapped(league))
			),
			secondaryButton: .cancel(
				TextState(Strings.Leagues.List.Delete.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

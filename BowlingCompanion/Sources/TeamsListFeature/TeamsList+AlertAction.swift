import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension TeamsList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Team)
		case dismissed
	}

	static func alert(toDelete team: Team) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(team.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(team))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

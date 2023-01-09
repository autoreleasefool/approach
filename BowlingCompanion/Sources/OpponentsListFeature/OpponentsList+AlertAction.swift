import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension OpponentsList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Opponent)
		case dismissed
	}

	static func alert(toDelete opponent: Opponent) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(opponent.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(opponent))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

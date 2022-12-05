import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension BowlersList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Bowler)
		case dismissed
	}

	static func alert(toDelete bowler: Bowler) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(bowler.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(bowler))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.discard),
				action: .send(.dismissed)
			)
		)
	}
}

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
			title: TextState(Strings.Bowlers.List.Delete.title(bowler.name)),
			primaryButton: .destructive(
				TextState(Strings.Bowlers.List.Delete.action),
				action: .send(.deleteButtonTapped(bowler))
			),
			secondaryButton: .cancel(
				TextState(Strings.Bowlers.List.Delete.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

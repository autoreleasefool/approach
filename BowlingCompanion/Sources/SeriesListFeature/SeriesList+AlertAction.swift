import ComposableArchitecture
import DateTimeLibrary
import StringsLibrary
import SharedModelsLibrary

extension SeriesList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Series)
		case dismissed
	}

	static func alert(toDelete series: Series) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(series.date.longFormat)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(series))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

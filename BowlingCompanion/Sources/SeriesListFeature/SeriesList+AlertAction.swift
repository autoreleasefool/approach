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
			title: TextState(Strings.Series.List.Delete.title(series.date.longFormat)),
			primaryButton: .destructive(
				TextState(Strings.Series.List.Delete.action),
				action: .send(.deleteButtonTapped(series))
			),
			secondaryButton: .cancel(
				TextState(Strings.Series.List.Delete.cancel),
				action: .send(.dismissed)
			)
		)
	}
}

import ComposableArchitecture
import DateTimeLibrary
import SharedModelsLibrary

extension SeriesList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Series)
		case dismissed
	}

	static func alert(toDelete series: Series) -> AlertState<AlertAction> {
		.init(
			title: TextState("Are you sure you want to delete \(series.date.longFormat)"),
			primaryButton: .destructive(
				TextState("Delete"),
				action: .send(.deleteButtonTapped(series))
			),
			secondaryButton: .cancel(
				TextState("Cancel"),
				action: .send(.dismissed)
			)
		)
	}
}

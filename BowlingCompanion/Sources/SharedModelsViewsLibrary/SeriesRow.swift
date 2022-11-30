import DateTimeLibrary
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct SeriesRow: View {
	let series: Series
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(series: Series, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.series = series
		self.onEdit = onEdit
		self.onDelete = onDelete
	}

	public var body: some View {
		Text(series.date.longFormat)
			.swipeActions(allowsFullSwipe: true) {
				if let onEdit {
					EditButton(perform: onEdit)
				}

				if let onDelete {
					DeleteButton(perform: onDelete)
				}
			}
	}
}

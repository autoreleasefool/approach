import DateTimeLibrary
import SharedModelsLibrary
import SwiftUI
import AssetsLibrary
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
			.frame(maxWidth: .infinity, alignment: .leading)
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

#if DEBUG
struct SeriesRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				SeriesRow(series: .init(league: .init(), id: .init(), date: .init(), numberOfGames: 4, alley: nil))
				SeriesRow(series: .init(league: .init(), id: .init(), date: .init(), numberOfGames: 6, alley: nil))
				SeriesRow(series: .init(league: .init(), id: .init(), date: .init(), numberOfGames: 2, alley: nil))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif

import DateTimeLibrary
import SharedModelsLibrary
import SwiftUI

public struct SeriesRow: View {
	let series: Series

	public init(series: Series) {
		self.series = series
	}

	public var body: some View {
		Text(series.date.longFormat)
	}
}

#if DEBUG
struct SeriesRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				SeriesRow(series: .init(
					league: .init(),
					id: .init(),
					date: .init(),
					numberOfGames: 4,
					preBowl: .regularPlay,
					excludeFromStatistics: .include,
					alley: nil,
					lane: nil
				))
				SeriesRow(series: .init(
					league: .init(),
					id: .init(),
					date: .init(),
					numberOfGames: 6,
					preBowl: .preBowl,
					excludeFromStatistics: .include,
					alley: nil,
					lane: nil
				))
				SeriesRow(series: .init(
					league: .init(),
					id: .init(),
					date: .init(),
					numberOfGames: 2,
					preBowl: .regularPlay,
					excludeFromStatistics: .exclude,
					alley: nil,
					lane: nil
				))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif

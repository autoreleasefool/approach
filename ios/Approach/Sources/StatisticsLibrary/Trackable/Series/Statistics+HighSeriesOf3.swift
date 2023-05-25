import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf3: Statistic, TrackablePerSeries {
		public static let title = Strings.Statistics.Title.highSeriesOf3
		public static let category: StatisticCategory = .series

		private var highSeries: Int = 0
		public var value: String { String(highSeries) }

		public init() {}

		public mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration) {
			guard bySeries.numberOfGames == 3 else { return }
			highSeries = max(highSeries, bySeries.total)
		}
	}
}

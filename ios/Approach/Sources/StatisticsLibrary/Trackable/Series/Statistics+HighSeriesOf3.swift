import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf3: Statistic, TrackablePerSeries {
		public static let title = Strings.Statistics.Title.highSeriesOf3
		public static let category: StatisticCategory = .series

		private var highSeries: Int
		public var value: String { String(highSeries) }

		public init() {
			self.init(highSeries: 0)
		}

		public init(highSeries: Int) {
			self.highSeries = highSeries
		}

		public mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration) {
			guard bySeries.numberOfGames == 3 else { return }
			highSeries = max(highSeries, bySeries.total)
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league: return true
			case .series, .game: return false
			}
		}
	}
}

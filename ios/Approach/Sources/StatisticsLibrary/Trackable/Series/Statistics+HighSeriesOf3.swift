import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf3: Statistic, TrackablePerSeries, HighestOfStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf3 }
		public static var category: StatisticCategory { .series }

		private var highSeries = 0
		public var highest: Int {
			get { highSeries }
			set { highSeries = newValue }
		}

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }

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

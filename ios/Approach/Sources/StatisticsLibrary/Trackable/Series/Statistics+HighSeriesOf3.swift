import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf3: Statistic, GraphableStatistic, TrackablePerSeries, GraphablePerSeries {
		public static let title = Strings.Statistics.Title.highSeriesOf3
		public static let category: StatisticCategory = .series

		private var highSeries: Int
		public var value: String { String(highSeries) }
		public var trackedValue: TrackedValue { .init(highSeries) }
		public var isEmpty: Bool { highSeries == 0 }

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

		public mutating func accumulate(by: any GraphableStatistic) {
			guard let by = by as? Self else { return }
			self.highSeries = max(by.highSeries, self.highSeries)
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league: return true
			case .series, .game: return false
			}
		}
	}
}

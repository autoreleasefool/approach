import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Twelves: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.twelves }
		public static var category: StatisticCategory { .twelves }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var twelves = 0
		public var count: Int {
			get { twelves }
			set { twelves = newValue }
		}

		public init() {}
		init(twelves: Int) { self.twelves = twelves }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isTwelve {
				twelves += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

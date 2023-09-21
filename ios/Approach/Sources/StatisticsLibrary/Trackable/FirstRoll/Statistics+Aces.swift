import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Aces: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.aces }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var aces = 0
		public var count: Int {
			get { aces }
			set { aces = newValue }
		}

		public init() {}
		init(aces: Int) { self.aces = aces }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isAce {
				aces += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

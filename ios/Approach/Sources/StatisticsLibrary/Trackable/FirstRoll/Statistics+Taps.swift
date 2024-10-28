import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Taps: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.taps }
		public static var category: StatisticCategory { .taps }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var taps = 0
		public var count: Int {
			get { taps }
			set { taps = newValue }
		}

		public init() {}
		init(taps: Int) { self.taps = taps }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isTapped {
				taps += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

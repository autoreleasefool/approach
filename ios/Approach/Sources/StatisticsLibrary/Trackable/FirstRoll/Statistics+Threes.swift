import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Threes: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.threes }
		public static var category: StatisticCategory { .threes }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var threes = 0
		public var count: Int {
			get { threes }
			set { threes = newValue }
		}

		public init() {}
		init(threes: Int) { self.threes = threes }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isThree {
				threes += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

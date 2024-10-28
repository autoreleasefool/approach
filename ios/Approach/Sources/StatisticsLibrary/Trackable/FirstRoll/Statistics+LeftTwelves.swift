import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftTwelves: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.leftTwelves }
		public static var category: StatisticCategory { .twelves }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var leftTwelves = 0
		public var count: Int {
			get { leftTwelves }
			set { leftTwelves = newValue }
		}

		public init() {}
		init(leftTwelves: Int) { self.leftTwelves = leftTwelves }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isLeftTwelve {
				leftTwelves += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

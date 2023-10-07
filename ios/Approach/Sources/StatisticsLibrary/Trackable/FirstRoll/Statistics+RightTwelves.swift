import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightTwelves: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.rightTwelves }
		public static var category: StatisticCategory { .twelves }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var rightTwelves = 0
		public var count: Int {
			get { rightTwelves }
			set { rightTwelves = newValue }
		}

		public init() {}
		init(rightTwelves: Int) { self.rightTwelves = rightTwelves }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isRightTwelve {
				rightTwelves += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightFives: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.rightFives }
		public static var category: StatisticCategory { .fives }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var rightFives = 0
		public var count: Int {
			get { rightFives }
			set { rightFives = newValue }
		}

		public init() {}
		init(rightFives: Int) { self.rightFives = rightFives }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isRightFive {
				rightFives += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

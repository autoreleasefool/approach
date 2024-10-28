import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftThrees: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.leftThrees }
		public static var category: StatisticCategory { .threes }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var leftThrees = 0
		public var count: Int {
			get { leftThrees }
			set { leftThrees = newValue }
		}

		public init() {}
		init(leftThrees: Int) { self.leftThrees = leftThrees }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isLeftThree {
				leftThrees += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftSplits: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.leftSplits }
		public static var category: StatisticCategory { .splits }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var leftSplits = 0
		public var count: Int {
			get { leftSplits }
			set { leftSplits = newValue }
		}

		public init() {}
		init(leftSplits: Int) { self.leftSplits = leftSplits }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isLeftSplit ||
					(configuration.countSplitWithBonusAsSplit && roll.roll.pinsDowned.isLeftSplitWithBonus) {
				leftSplits += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

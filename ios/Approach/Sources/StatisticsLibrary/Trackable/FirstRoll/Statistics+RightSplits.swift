import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightSplits: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.rightSplits }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var rightSplits = 0
		public var count: Int {
			get { rightSplits }
			set { rightSplits = newValue }
		}

		public init() {}
		init(rightSplits: Int) { self.rightSplits = rightSplits }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isRightSplit ||
					(configuration.countSplitWithBonusAsSplit && roll.roll.pinsDowned.isRightSplitWithBonus) {
				rightSplits += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

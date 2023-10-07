import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightOfMiddleHits: Statistic, TrackablePerFirstRoll, FirstRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightOfMiddleHits }
		public static var category: StatisticCategory { .middleHits }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		public var totalRolls = 0
		private var rightOfMiddleHits = 0

		public var numerator: Int {
			get { rightOfMiddleHits }
			set { rightOfMiddleHits = newValue }
		}

		public init() {}
		init(rightOfMiddleHits: Int, totalRolls: Int) {
			self.rightOfMiddleHits = rightOfMiddleHits
			self.totalRolls = totalRolls
		}

		public mutating func tracks(firstRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) -> Bool {
			firstRoll.roll.pinsDowned.isHitRightOfMiddle
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftOfMiddleHits: Statistic, TrackablePerFirstRoll, FirstRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftOfMiddleHits }
		public static var category: StatisticCategory { .middleHits }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		public var totalRolls = 0
		private var leftOfMiddleHits = 0

		public var numerator: Int {
			get { leftOfMiddleHits }
			set { leftOfMiddleHits = newValue }
		}

		public init() {}
		init(leftOfMiddleHits: Int, totalRolls: Int) {
			self.leftOfMiddleHits = leftOfMiddleHits
			self.totalRolls = totalRolls
		}

		public mutating func tracks(firstRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) -> Bool {
			firstRoll.roll.pinsDowned.isHitLeftOfMiddle
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

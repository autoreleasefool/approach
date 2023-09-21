import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct MiddleHits: Statistic, TrackablePerFirstRoll, FirstRollStatistic {
		public static var title: String { Strings.Statistics.Title.middleHits }
		public static var category: StatisticCategory { .overall }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public var totalRolls = 0
		private var middleHits = 0

		public var numerator: Int {
			get { middleHits }
			set { middleHits = newValue }
		}

		public init() {}
		init(middleHits: Int, totalRolls: Int) {
			self.middleHits = middleHits
			self.totalRolls = totalRolls
		}

		public mutating func tracks(firstRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) -> Bool {
			firstRoll.roll.pinsDowned.isMiddleHit
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

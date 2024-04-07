import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct AverageStrikeCount: Statistic, TrackablePerFrameSet, AveragingStatistic {
		public static var title: String { Strings.Statistics.Title.averageStrikeCount }
		public static var category: StatisticCategory { .strikesAndSpares }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		private var totalStrikeEarnedCount = 0
		private var totalStrikes = 0

		public var total: Int {
			get { totalStrikeEarnedCount }
			set { totalStrikeEarnedCount = newValue }
		}

		public var divisor: Int {
			get { totalStrikes }
			set { totalStrikes = newValue }
		}

		public init() {}
		init(totalStrikeEarnedCount: Int, totalStrikes: Int) {
			self.totalStrikeEarnedCount = totalStrikeEarnedCount
			self.totalStrikes = totalStrikes
		}

		public mutating func adjust(byFrameSet: [Frame.TrackableEntry], configuration: TrackablePerFrameSetConfiguration) {
			if byFrameSet.first?.
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: true
			}
		}
	}
}

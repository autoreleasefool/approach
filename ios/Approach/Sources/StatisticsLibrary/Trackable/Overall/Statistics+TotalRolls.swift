import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct TotalRolls: Statistic, TrackablePerFrame, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.totalRolls }
		public static var category: StatisticCategory { .overall }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { nil }

		private var totalRolls = 0
		public var count: Int {
			get { totalRolls }
			set { totalRolls = newValue }
		}

		public init() {}
		init(totalRolls: Int) { self.totalRolls = totalRolls }

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration _: TrackablePerFrameConfiguration) {
			totalRolls += byFrame.totalRolls
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

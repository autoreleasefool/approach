import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Fives: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.fives }
		public static var category: StatisticCategory { .fives }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var fives = 0
		public var count: Int {
			get { fives }
			set { fives = newValue }
		}

		public init() {}
		init(fives: Int) { self.fives = fives }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isFive {
				fives += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

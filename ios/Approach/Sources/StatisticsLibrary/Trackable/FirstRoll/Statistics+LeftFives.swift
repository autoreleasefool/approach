import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftFives: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.leftFives }
		public static var category: StatisticCategory { .fives }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var leftFives = 0
		public var count: Int {
			get { leftFives }
			set { leftFives = newValue }
		}

		public init() {}
		init(leftFives: Int) { self.leftFives = leftFives }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isLeftFive {
				leftFives += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

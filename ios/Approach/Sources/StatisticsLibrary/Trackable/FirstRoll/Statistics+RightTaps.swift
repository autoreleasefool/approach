import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightTaps: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.rightTaps }
		public static var category: StatisticCategory { .taps }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var rights = 0
		public var count: Int {
			get { rights }
			set { rights = newValue }
		}

		public init() {}
		init(rights: Int) { self.rights = rights }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isRight {
				rights += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

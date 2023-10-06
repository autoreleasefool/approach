import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightThrees: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.rightThrees }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var rightThrees = 0
		public var count: Int {
			get { rightThrees }
			set { rightThrees = newValue }
		}

		public init() {}
		init(rightThrees: Int) { self.rightThrees = rightThrees }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isRightThree {
				rightThrees += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

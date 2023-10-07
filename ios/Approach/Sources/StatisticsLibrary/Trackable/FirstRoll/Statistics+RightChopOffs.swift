import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightChopOffs: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.rightChopOffs }
		public static var category: StatisticCategory { .chopOffs }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var rightChopOffs = 0
		public var count: Int {
			get { rightChopOffs }
			set { rightChopOffs = newValue }
		}

		public init() {}
		init(rightChopOffs: Int) { self.rightChopOffs = rightChopOffs }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isRightChopOff {
				rightChopOffs += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

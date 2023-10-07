import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftChopOffs: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.leftChopOffs }
		public static var category: StatisticCategory { .chopOffs }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var leftChopOffs = 0
		public var count: Int {
			get { leftChopOffs }
			set { leftChopOffs = newValue }
		}

		public init() {}
		init(leftChopOffs: Int) { self.leftChopOffs = leftChopOffs }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isLeftChopOff {
				leftChopOffs += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

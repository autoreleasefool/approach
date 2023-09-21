import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct ChopOffs: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.chopOffs }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var chopOffs = 0
		public var count: Int {
			get { chopOffs }
			set { chopOffs = newValue }
		}

		public init() {}
		init(chopOffs: Int) { self.chopOffs = chopOffs }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isChopOff {
				chopOffs += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

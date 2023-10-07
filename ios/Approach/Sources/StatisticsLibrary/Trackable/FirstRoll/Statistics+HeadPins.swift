import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HeadPins: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.headPins }
		public static var category: StatisticCategory { .headPins }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		private var headPins = 0
		public var count: Int {
			get { headPins }
			set { headPins = newValue }
		}

		public init() {}
		init(headPins: Int) { self.headPins = headPins }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isHeadPin || (configuration.countHeadPin2AsHeadPin && roll.roll.pinsDowned.isHeadPin2) {
				headPins += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

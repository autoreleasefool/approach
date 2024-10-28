import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct FirstRollAverage: Statistic, TrackablePerFirstRoll, AveragingStatistic {
		public static var title: String { Strings.Statistics.Title.firstRollAverage }
		public static var category: StatisticCategory { .firstRoll }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		private var totalFirstRollPinfall = 0
		private var totalFirstRolls = 0

		public var total: Int {
			get { totalFirstRollPinfall }
			set { totalFirstRollPinfall = newValue }
		}

		public var divisor: Int {
			get { totalFirstRolls }
			set { totalFirstRolls = newValue }
		}

		public init() {}
		init(totalFirstRollPinfall: Int, totalFirstRolls: Int) {
			self.totalFirstRollPinfall = totalFirstRollPinfall
			self.totalFirstRolls = totalFirstRolls
		}

		public mutating func adjust(byFirstRoll: Frame.OrderedRoll, configuration _: TrackablePerFrameConfiguration) {
			totalFirstRollPinfall += byFirstRoll.roll.pinsDowned.value
			totalFirstRolls += 1
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: true
			}
		}
	}
}

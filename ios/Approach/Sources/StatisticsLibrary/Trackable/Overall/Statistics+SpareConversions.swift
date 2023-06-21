import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct SpareConversions: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.spareConversions }
		public static var category: StatisticCategory { .overall }

		public var totalSecondRolls = 0
		private var spares = 0

		public var numerator: Int {
			get { spares }
			set { spares = newValue }
		}

		public init() {}
		init(spares: Int, totalSecondRolls: Int) {
			self.spares = spares
			self.totalSecondRolls = totalSecondRolls
		}

		public mutating func tracks(secondRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) -> Bool {
			secondRoll.roll.pinsDowned.arePinsCleared
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

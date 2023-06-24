import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Splits: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.splits }
		public static var category: StatisticCategory { .onFirstRoll }

		private var splits = 0
		public var count: Int {
			get { splits }
			set { splits = newValue }
		}

		public init() {}
		init(splits: Int) { self.splits = splits }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isSplit ||
					(configuration.countSplitWithBonusAsSplit && roll.roll.pinsDowned.isSplitWithBonus) {
				splits += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

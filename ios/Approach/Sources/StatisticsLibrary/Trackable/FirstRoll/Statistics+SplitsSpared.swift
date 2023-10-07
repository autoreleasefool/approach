import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct SplitsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.splitsSpared }
		public static var category: StatisticCategory { .splits }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.splits }

		private var splits = 0
		private var splitsSpared = 0

		public var numerator: Int {
			get { splitsSpared }
			set { splitsSpared = newValue }
		}

		public var denominator: Int {
			get { splits }
			set { splits = newValue }
		}

		public init() {}
		init(splits: Int, splitsSpared: Int) {
			self.splits = splits
			self.splitsSpared = splitsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isSplit || (configuration.countSplitWithBonusAsSplit && firstRoll.isSplitWithBonus) {
				splits += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					splitsSpared += 1
				}
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

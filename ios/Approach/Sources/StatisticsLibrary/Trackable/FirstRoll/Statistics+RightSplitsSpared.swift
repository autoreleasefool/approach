import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightSplitsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightSplitsSpared }
		public static var category: StatisticCategory { .splits }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.rightSplits }

		private var rightSplits = 0
		private var rightSplitsSpared = 0

		public var numerator: Int {
			get { rightSplitsSpared }
			set { rightSplitsSpared = newValue }
		}

		public var denominator: Int {
			get { rightSplits }
			set { rightSplits = newValue }
		}

		public init() {}
		init(rightSplits: Int, rightSplitsSpared: Int) {
			self.rightSplits = rightSplits
			self.rightSplitsSpared = rightSplitsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isRightSplit || (configuration.countSplitWithBonusAsSplit && firstRoll.isRightSplitWithBonus) {
				rightSplits += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					rightSplitsSpared += 1
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

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftSplitsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftSplitsSpared }
		public static var category: StatisticCategory { .onFirstRoll }

		public static var denominatorTitle: String { Strings.Statistics.Title.leftSplits }

		private var leftSplits = 0
		private var leftSplitsSpared = 0

		public var numerator: Int {
			get { leftSplitsSpared }
			set { leftSplitsSpared = newValue }
		}

		public var denominator: Int {
			get { leftSplits }
			set { leftSplits = newValue }
		}

		public init() {}
		init(leftSplits: Int, leftSplitsSpared: Int) {
			self.leftSplits = leftSplits
			self.leftSplitsSpared = leftSplitsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isLeftSplit || (configuration.countSplitWithBonusAsSplit && firstRoll.isLeftSplitWithBonus) {
				leftSplits += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					leftSplitsSpared += 1
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

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightFivesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightFivesSpared }
		public static var category: StatisticCategory { .fives }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.rightFives }

		private var rightFives = 0
		private var rightFivesSpared = 0

		public var numerator: Int {
			get { rightFivesSpared }
			set { rightFivesSpared = newValue }
		}

		public var denominator: Int {
			get { rightFives }
			set { rightFives = newValue }
		}

		public init() {}
		init(rightFives: Int, rightFivesSpared: Int) {
			self.rightFives = rightFives
			self.rightFivesSpared = rightFivesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isRightFive {
				rightFives += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					rightFivesSpared += 1
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

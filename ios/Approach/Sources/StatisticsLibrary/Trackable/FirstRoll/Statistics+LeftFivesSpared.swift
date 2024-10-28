import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftFivesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftFivesSpared }
		public static var category: StatisticCategory { .fives }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.leftFives }

		private var leftFives = 0
		private var leftFivesSpared = 0

		public var numerator: Int {
			get { leftFivesSpared }
			set { leftFivesSpared = newValue }
		}

		public var denominator: Int {
			get { leftFives }
			set { leftFives = newValue }
		}

		public init() {}
		init(leftFives: Int, leftFivesSpared: Int) {
			self.leftFives = leftFives
			self.leftFivesSpared = leftFivesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration _: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isLeftFive {
				leftFives += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					leftFivesSpared += 1
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

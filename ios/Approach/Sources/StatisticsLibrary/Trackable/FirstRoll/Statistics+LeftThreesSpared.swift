import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftThreesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftThreesSpared }
		public static var category: StatisticCategory { .threes }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.leftThrees }

		private var leftThrees = 0
		private var leftThreesSpared = 0

		public var numerator: Int {
			get { leftThreesSpared }
			set { leftThreesSpared = newValue }
		}

		public var denominator: Int {
			get { leftThrees }
			set { leftThrees = newValue }
		}

		public init() {}
		init(leftThrees: Int, leftThreesSpared: Int) {
			self.leftThrees = leftThrees
			self.leftThreesSpared = leftThreesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isLeftThree {
				leftThrees += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					leftThreesSpared += 1
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

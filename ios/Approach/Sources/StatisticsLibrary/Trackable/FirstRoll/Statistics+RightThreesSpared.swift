import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightThreesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightThreesSpared }
		public static var category: StatisticCategory { .threes }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.rightThrees }

		private var rightThrees = 0
		private var rightThreesSpared = 0

		public var numerator: Int {
			get { rightThreesSpared }
			set { rightThreesSpared = newValue }
		}

		public var denominator: Int {
			get { rightThrees }
			set { rightThrees = newValue }
		}

		public init() {}
		init(rightThrees: Int, rightThreesSpared: Int) {
			self.rightThrees = rightThrees
			self.rightThreesSpared = rightThreesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isRightThree {
				rightThrees += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					rightThreesSpared += 1
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

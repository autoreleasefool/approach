import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct ThreesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.threesSpared }
		public static var category: StatisticCategory { .threes }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.threes }

		private var threes = 0
		private var threesSpared = 0

		public var numerator: Int {
			get { threesSpared }
			set { threesSpared = newValue }
		}

		public var denominator: Int {
			get { threes }
			set { threes = newValue }
		}

		public init() {}
		init(threes: Int, threesSpared: Int) {
			self.threes = threes
			self.threesSpared = threesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration _: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isThree {
				threes += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					threesSpared += 1
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

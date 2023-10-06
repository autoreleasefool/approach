import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct FivesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.fivesSpared }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.fives }

		private var fives = 0
		private var fivesSpared = 0

		public var numerator: Int {
			get { fivesSpared }
			set { fivesSpared = newValue }
		}

		public var denominator: Int {
			get { fives }
			set { fives = newValue }
		}

		public init() {}
		init(fives: Int, fivesSpared: Int) {
			self.fives = fives
			self.fivesSpared = fivesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isFive {
				fives += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					fivesSpared += 1
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

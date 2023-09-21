import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftTwelvesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftTwelvesSpared }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.leftTwelves }

		private var leftTwelves = 0
		private var leftTwelvesSpared = 0

		public var numerator: Int {
			get { leftTwelvesSpared }
			set { leftTwelvesSpared = newValue }
		}

		public var denominator: Int {
			get { leftTwelves }
			set { leftTwelves = newValue }
		}

		public init() {}
		init(leftTwelves: Int, leftTwelvesSpared: Int) {
			self.leftTwelves = leftTwelves
			self.leftTwelvesSpared = leftTwelvesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isLeftTwelve {
				leftTwelves += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					leftTwelvesSpared += 1
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

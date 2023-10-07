import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftsSpared }
		public static var category: StatisticCategory { .taps }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.lefts }

		private var lefts = 0
		private var leftsSpared = 0

		public var numerator: Int {
			get { leftsSpared }
			set { leftsSpared = newValue }
		}

		public var denominator: Int {
			get { lefts }
			set { lefts = newValue }
		}

		public init() {}
		init(lefts: Int, leftsSpared: Int) {
			self.lefts = lefts
			self.leftsSpared = leftsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isLeft {
				lefts += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					leftsSpared += 1
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

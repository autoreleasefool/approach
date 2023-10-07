import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct TwelvesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.twelvesSpared }
		public static var category: StatisticCategory { .twelves }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.twelves }

		private var twelves = 0
		private var twelvesSpared = 0

		public var numerator: Int {
			get { twelvesSpared }
			set { twelvesSpared = newValue }
		}

		public var denominator: Int {
			get { twelves }
			set { twelves = newValue }
		}

		public init() {}
		init(twelves: Int, twelvesSpared: Int) {
			self.twelves = twelves
			self.twelvesSpared = twelvesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isTwelve {
				twelves += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					twelvesSpared += 1
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

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct TapsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.tapsSpared }
		public static var category: StatisticCategory { .taps }
		public static var isEligibleForNewLabel: Bool { true }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.taps }

		private var taps = 0
		private var tapsSpared = 0

		public var numerator: Int {
			get { tapsSpared }
			set { tapsSpared = newValue }
		}

		public var denominator: Int {
			get { taps }
			set { taps = newValue }
		}

		public init() {}
		init(taps: Int, tapsSpared: Int) {
			self.taps = taps
			self.tapsSpared = tapsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration _: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isTapped {
				taps += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					tapsSpared += 1
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

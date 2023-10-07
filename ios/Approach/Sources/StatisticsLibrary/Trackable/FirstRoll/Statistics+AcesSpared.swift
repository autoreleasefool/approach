import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct AcesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.acesSpared }
		public static var category: StatisticCategory { .aces }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.aces }

		private var aces = 0
		private var acesSpared = 0

		public var numerator: Int {
			get { acesSpared }
			set { acesSpared = newValue }
		}

		public var denominator: Int {
			get { aces }
			set { aces = newValue }
		}

		public init() {}
		init(aces: Int, acesSpared: Int) {
			self.aces = aces
			self.acesSpared = acesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isAce {
				aces += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					acesSpared += 1
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

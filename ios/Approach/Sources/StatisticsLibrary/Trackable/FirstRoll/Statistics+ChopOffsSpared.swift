import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct ChopOffsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.chopOffsSpared }
		public static var category: StatisticCategory { .chopOffs }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.chopOffs }

		private var chopOffs = 0
		private var chopOffsSpared = 0

		public var numerator: Int {
			get { chopOffsSpared }
			set { chopOffsSpared = newValue }
		}

		public var denominator: Int {
			get { chopOffs }
			set { chopOffs = newValue }
		}

		public init() {}
		init(chopOffs: Int, chopOffsSpared: Int) {
			self.chopOffs = chopOffs
			self.chopOffsSpared = chopOffsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isChopOff {
				chopOffs += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					chopOffsSpared += 1
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

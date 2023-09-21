import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightsSpared }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.rights }

		private var rights = 0
		private var rightsSpared = 0

		public var numerator: Int {
			get { rightsSpared }
			set { rightsSpared = newValue }
		}

		public var denominator: Int {
			get { rights }
			set { rights = newValue }
		}

		public init() {}
		init(rights: Int, rightsSpared: Int) {
			self.rights = rights
			self.rightsSpared = rightsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isRight {
				rights += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					rightsSpared += 1
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

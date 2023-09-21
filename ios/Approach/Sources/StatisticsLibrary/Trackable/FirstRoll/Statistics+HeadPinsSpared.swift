import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HeadPinsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.headPinsSpared }
		public static var category: StatisticCategory { .onFirstRoll }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var denominatorTitle: String { Strings.Statistics.Title.headPins }

		private var headPins = 0
		private var headPinsSpared = 0

		public var numerator: Int {
			get { headPinsSpared }
			set { headPinsSpared = newValue }
		}

		public var denominator: Int {
			get { headPins }
			set { headPins = newValue }
		}

		public init() {}
		init(headPins: Int, headPinsSpared: Int) {
			self.headPins = headPins
			self.headPinsSpared = headPinsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			guard firstRoll.isHeadPin || (configuration.countHeadPin2AsHeadPin && firstRoll.isHeadPin2) else { return }

			headPins += 1

			if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
				headPinsSpared += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

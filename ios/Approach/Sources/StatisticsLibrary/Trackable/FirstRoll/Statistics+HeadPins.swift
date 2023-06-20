import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HeadPins: Statistic, TrackablePerFrame, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.headPins }
		public static var category: StatisticCategory { .onFirstRoll }

		private var headPins = 0
		public var count: Int {
			get { headPins }
			set { headPins = newValue }
		}

		public init() {}
		init(headPins: Int) { self.headPins = headPins }

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
			for roll in byFrame.firstRolls {
				if roll.roll.pinsDowned.isHeadPin || (configuration.countHeadPin2AsHeadPin && roll.roll.pinsDowned.isHeadPin2) {
					headPins += 1
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

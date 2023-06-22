import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Lefts: Statistic, TrackablePerFirstRoll, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.lefts }
		public static var category: StatisticCategory { .onFirstRoll }

		private var lefts = 0
		public var count: Int {
			get { lefts }
			set { lefts = newValue }
		}

		public init() {}
		init(lefts: Int) { self.lefts = lefts }

		public mutating func adjust(byFirstRoll roll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
			if roll.roll.pinsDowned.isLeft {
				lefts += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}

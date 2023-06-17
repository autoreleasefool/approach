import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSingle: Statistic, GraphableStatistic, TrackablePerGame, HighestOfStatistic {
		public var title: String { Strings.Statistics.Title.highSingle }
		public var category: StatisticCategory { .overall }

		private var highSingle = 0
		public var highest: Int {
			get { highSingle }
			set { highSingle = newValue }
		}

		public init() {}
		init(highSingle: Int) { self.highSingle = highSingle }

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			highSingle = max(highSingle, byGame.score)
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}

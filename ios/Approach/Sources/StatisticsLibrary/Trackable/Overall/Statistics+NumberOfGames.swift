import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct NumberOfGames: Statistic, TrackablePerGame, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.numberOfGames }
		public static var category: StatisticCategory { .overall }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { nil }

		private var numberOfGames = 0
		public var count: Int {
			get { numberOfGames }
			set { numberOfGames = newValue }
		}

		public init() {}
		init(numberOfGames: Int) { self.numberOfGames = numberOfGames }

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			guard byGame.score > 0 else { return }
			numberOfGames += 1
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}

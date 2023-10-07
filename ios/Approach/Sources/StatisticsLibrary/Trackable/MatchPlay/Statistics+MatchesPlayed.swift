import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct MatchesPlayed: Statistic, TrackablePerGame, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.matchesPlayed }
		public static var category: StatisticCategory { .matchPlayResults }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { nil }

		private var matchesPlayed = 0
		public var count: Int {
			get { matchesPlayed }
			set { matchesPlayed = newValue }
		}

		public init() {}
		init(matchesPlayed: Int) { self.matchesPlayed = matchesPlayed }

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			if byGame.matchPlay != nil {
				matchesPlayed += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}

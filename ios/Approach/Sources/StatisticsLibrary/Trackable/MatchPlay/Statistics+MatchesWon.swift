import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct MatchesWon: Statistic, TrackablePerGame, PercentageStatistic {
		public static var title: String { Strings.Statistics.Title.matchesWon }
		public static var category: StatisticCategory { .matchPlayResults }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		public static var numeratorTitle: String { Strings.Statistics.Title.matchesWon }
		public static var denominatorTitle: String { Strings.Statistics.Title.matchesPlayed }
		public static var includeNumeratorInFormattedValue: Bool { true }

		private var matchesPlayed = 0
		private var matchesWon = 0

		public var numerator: Int {
			get { matchesWon }
			set { matchesWon = newValue }
		}

		public var denominator: Int {
			get { matchesPlayed }
			set { matchesPlayed = newValue }
		}

		public init() {}
		init(matchesPlayed: Int, matchesWon: Int) {
			self.matchesPlayed = matchesPlayed
			self.matchesWon = matchesWon
		}

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			guard let matchPlay = byGame.matchPlay else { return }
			matchesPlayed += 1
			switch matchPlay.result {
			case .won:
				matchesWon += 1
			case .lost, .tied, .none:
				break
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

import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct MatchesLost: Statistic, TrackablePerGame, PercentageStatistic {
		public static var title: String { Strings.Statistics.Title.matchesLost }
		public static var category: StatisticCategory { .matchPlayResults }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		public static var numeratorTitle: String { Strings.Statistics.Title.matchesLost }
		public static var denominatorTitle: String { Strings.Statistics.Title.matchesPlayed }
		public static var includeNumeratorInFormattedValue: Bool { true }

		private var matchesPlayed = 0
		private var matchesLost = 0

		public var numerator: Int {
			get { matchesLost }
			set { matchesLost = newValue }
		}

		public var denominator: Int {
			get { matchesPlayed }
			set { matchesPlayed = newValue }
		}

		public init() {}
		init(matchesPlayed: Int, matchesLost: Int) {
			self.matchesPlayed = matchesPlayed
			self.matchesLost = matchesLost
		}

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			guard let matchPlay = byGame.matchPlay else { return }
			matchesPlayed += 1
			switch matchPlay.result {
			case .lost:
				matchesLost += 1
			case .won, .tied, .none:
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

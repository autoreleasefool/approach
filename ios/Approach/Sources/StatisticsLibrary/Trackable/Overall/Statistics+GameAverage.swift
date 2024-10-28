import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct GameAverage: Statistic, TrackablePerGame, AveragingStatistic {
		public static var title: String { Strings.Statistics.Title.gameAverage }
		public static var category: StatisticCategory { .overall }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

		private var totalPinFall = 0
		private var gamesPlayed = 0

		public var total: Int {
			get { totalPinFall }
			set { totalPinFall = newValue }
		}

		public var divisor: Int {
			get { gamesPlayed }
			set { gamesPlayed = newValue }
		}

		public init() {}
		init(totalPinfall: Int, gamesPlayed: Int) {
			self.totalPinFall = totalPinfall
			self.gamesPlayed = gamesPlayed
		}

		public mutating func adjust(byGame: Game.TrackableEntry, configuration _: TrackablePerGameConfiguration) {
			guard byGame.score > 0 else { return }
			totalPinFall += byGame.score
			gamesPlayed += 1
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}

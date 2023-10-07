import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct TotalPinfall: Statistic, TrackablePerGame, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.totalPinfall }
		public static var category: StatisticCategory { .overall }
		public static var isEligibleForNewLabel: Bool { false }
		public static var preferredTrendDirection: StatisticTrendDirection? { nil }

		private var totalPinfall = 0
		public var count: Int {
			get { totalPinfall }
			set { totalPinfall = newValue }
		}

		public init() {}
		init(totalPinfall: Int) { self.totalPinfall = totalPinfall }

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			totalPinfall += byGame.score
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}

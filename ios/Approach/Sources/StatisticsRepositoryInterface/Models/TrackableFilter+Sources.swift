import ModelsLibrary
import StatisticsLibrary

extension TrackableFilter {
	public struct Sources: Equatable {
		public let bowler: Bowler.Summary?
		public let league: League.Summary?
		public let series: Series.Summary?
		public let game: Game.Summary?

		public init(bowler: Bowler.Summary?, league: League.Summary?, series: Series.Summary?, game: Game.Summary?) {
			self.bowler = bowler
			self.league = league
			self.series = series
			self.game = game
		}
	}
}

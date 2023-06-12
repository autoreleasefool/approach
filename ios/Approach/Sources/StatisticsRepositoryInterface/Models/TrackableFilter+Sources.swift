import ModelsLibrary
import StatisticsLibrary

extension TrackableFilter {
	public struct Sources: Equatable {
		public var bowler: Bowler.Summary
		public var league: League.Summary?
		public var series: Series.Summary?
		public var game: Game.Summary?

		public init(bowler: Bowler.Summary, league: League.Summary?, series: Series.Summary?, game: Game.Summary?) {
			self.bowler = bowler
			self.league = league
			self.series = series
			self.game = game
		}
	}
}

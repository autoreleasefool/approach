import ModelsLibrary

extension Game {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Game.ID
		public let index: Int
		public let bowler: Bowler.Summary
		public let league: League.Summary
		public let series: Series.Summary

		public var locked: Game.Lock
		public var manualScore: Int?
		public var excludeFromStatistics: Game.ExcludeFromStatistics

		public init(
			id: Game.ID,
			index: Int,
			locked: Game.Lock,
			manualScore: Int?,
			excludeFromStatistics: Game.ExcludeFromStatistics,
			bowler: Bowler.Summary,
			league: League.Summary,
			series: Series.Summary
		) {
			self.id = id
			self.index = index
			self.locked = locked
			self.manualScore = manualScore
			self.excludeFromStatistics = excludeFromStatistics
			self.bowler = bowler
			self.league = league
			self.series = series
		}
	}
}

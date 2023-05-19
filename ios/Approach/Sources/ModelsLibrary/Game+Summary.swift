extension Game {
	public struct Summary: Identifiable, Equatable, Codable {
		public let id: Game.ID
		public let index: Int
		public let score: Int
		public let scoringMethod: ScoringMethod
	}
}

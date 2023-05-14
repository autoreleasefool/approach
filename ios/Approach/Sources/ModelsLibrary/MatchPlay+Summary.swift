extension MatchPlay {
	public struct Summary: Identifiable, Codable, Equatable {
		public let gameId: Game.ID
		public let id: MatchPlay.ID
		public let opponent: Bowler.Summary
		public let opponentScore: Int?
		public let result: MatchPlay.Result?
	}
}

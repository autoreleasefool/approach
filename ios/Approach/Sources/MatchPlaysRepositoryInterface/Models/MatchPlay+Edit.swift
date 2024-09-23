import ModelsLibrary

extension MatchPlay {
	public struct Edit: Identifiable, Codable, Equatable, Sendable {
		public let gameId: Game.ID
		public let id: MatchPlay.ID

		public var opponent: Bowler.Summary?
		public var opponentScore: Int?
		public var result: MatchPlay.Result?

		public init(
			gameId: Game.ID,
			id: MatchPlay.ID,
			opponent: Bowler.Summary? = nil,
			opponentScore: Int? = nil,
			result: MatchPlay.Result? = nil
		) {
			self.gameId = gameId
			self.id = id
			self.opponent = opponent
			self.opponentScore = opponentScore
			self.result = result
		}
	}
}

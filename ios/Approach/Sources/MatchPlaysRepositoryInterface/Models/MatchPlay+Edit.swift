import ModelsLibrary

extension MatchPlay {
	public struct Edit: Identifiable, Codable, Equatable {
		public let gameId: Game.ID
		public let id: MatchPlay.ID

		public var opponent: Bowler.Summary?
		public var opponentScore: Int?
		public var result: MatchPlay.Result?
	}
}

extension Game {
	public struct Summary: Identifiable, Equatable, Codable {
		public let id: Game.ID
		public let ordinal: Int
		public let manualScore: Int?
	}
}

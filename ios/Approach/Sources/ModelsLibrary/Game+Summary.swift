extension Game {
	public struct Summary: Identifiable, Equatable, Codable {
		public let id: Game.ID
		public let ordinal: Int
		public let manualScore: Int?

		public init(
			id: Game.ID,
			ordinal: Int,
			manualScore: Int?
		) {
			self.id = id
			self.ordinal = ordinal
			self.manualScore = manualScore
		}
	}
}

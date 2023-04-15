import ModelsLibrary

extension Game {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Game.ID
		public var locked: Game.Lock
		public var manualScore: Int?
		public var excludeFromStatistics: Game.ExcludeFromStatistics

		public init(
			id: Game.ID,
			locked: Game.Lock,
			manualScore: Int?,
			excludeFromStatistics: Game.ExcludeFromStatistics
		) {
			self.id = id
			self.locked = locked
			self.manualScore = manualScore
			self.excludeFromStatistics = excludeFromStatistics
		}
	}
}

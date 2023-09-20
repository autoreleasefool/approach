import ModelsLibrary

extension Game {
	public struct Indexed: Identifiable, Codable, Equatable {
		public let id: Game.ID
		public let index: Int

		public init(id: Game.ID, index: Int) {
			self.id = id
			self.index = index
		}
	}
}

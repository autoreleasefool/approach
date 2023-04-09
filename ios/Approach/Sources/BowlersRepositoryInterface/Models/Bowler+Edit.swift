import ModelsLibrary

extension Bowler {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String

		public init(id: Bowler.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

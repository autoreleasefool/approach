extension League {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let name: String

		public init(id: League.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

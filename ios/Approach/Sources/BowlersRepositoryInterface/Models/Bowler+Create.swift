import ModelsLibrary

extension Bowler {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String
		public let status: Bowler.Status

		public init(id: Bowler.ID, name: String, status: Bowler.Status) {
			self.id = id
			self.name = name
			self.status = status
		}
	}
}

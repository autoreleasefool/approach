extension Lane {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Lane.ID
		public let label: String
		public let position: Lane.Position
	}
}

extension Gear {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public let name: String
		public let kind: Kind
		public let ownerName: String?
	}

	public struct Rolled: Identifiable, Codable, Equatable, Sendable {
		public let id: Gear.ID
		public let name: String
	}
}

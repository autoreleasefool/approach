extension Gear {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public let name: String
		public let kind: Kind
		public let ownerName: String?
	}
}

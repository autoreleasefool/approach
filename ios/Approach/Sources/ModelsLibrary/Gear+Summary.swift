extension Gear {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public let name: String
		public let kind: Kind
		public let ownerName: String?

		public init(
			id: Gear.ID,
			name: String,
			kind: Kind,
			ownerName: String?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.ownerName = ownerName
		}
	}
}

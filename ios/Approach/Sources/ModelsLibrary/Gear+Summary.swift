extension Gear {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public let name: String
		public let kind: Kind
		public let ownerName: String?

		public var rolled: Rolled {
			.init(id: id, name: name)
		}
	}

	public struct Rolled: Identifiable, Codable, Equatable, Sendable {
		public let id: Gear.ID
		public let name: String
	}
}

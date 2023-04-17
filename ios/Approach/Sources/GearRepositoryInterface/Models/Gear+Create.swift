import ModelsLibrary

extension Gear {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var owner: Bowler.Summary?

		public init(
			id: Gear.ID,
			name: String,
			kind: Kind,
			owner: Bowler.Summary?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.owner = owner
		}
	}
}

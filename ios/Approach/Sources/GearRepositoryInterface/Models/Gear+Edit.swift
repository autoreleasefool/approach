import ModelsLibrary

extension Gear {
	public struct Edit: Identifiable, Equatable, Codable {
		public let id: Gear.ID
		public var name: String
		public var owner: Bowler.Summary?

		public init(
			id: Gear.ID,
			name: String,
			owner: Bowler.Summary?
		) {
			self.id = id
			self.name = name
			self.owner = owner
		}
	}
}

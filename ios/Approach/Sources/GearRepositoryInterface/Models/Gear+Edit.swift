import ModelsLibrary

extension Gear {
	public struct Edit: Identifiable, Equatable, Codable {
		public let id: Gear.ID
		public var name: String
		public var bowler: Bowler.ID?

		public init(
			id: Gear.ID,
			name: String,
			bowler: Bowler.ID?
		) {
			self.id = id
			self.name = name
			self.bowler = bowler
		}
	}
}

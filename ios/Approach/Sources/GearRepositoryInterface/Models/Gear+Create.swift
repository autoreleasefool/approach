import ModelsLibrary

extension Gear {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var bowler: Bowler.ID?

		public init(
			id: Gear.ID,
			name: String,
			kind: Kind,
			bowler: Bowler.ID?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.bowler = bowler
		}
	}
}

import ModelsLibrary

extension Lane {
	public struct Create: Identifiable, Codable, Equatable {
		public let alley: Alley.ID
		public let id: Lane.ID
		public var label: String
		public var position: Lane.Position

		public init(
			alley: Alley.ID,
			id: Lane.ID,
			label: String,
			position: Lane.Position
		) {
			self.alley = alley
			self.id = id
			self.label = label
			self.position = position
		}
	}
}

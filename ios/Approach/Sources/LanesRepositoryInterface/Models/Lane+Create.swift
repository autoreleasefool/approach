import ModelsLibrary

extension Lane {
	public struct Create: Identifiable, Codable, Equatable {
		public let alleyId: Alley.ID
		public let id: Lane.ID
		public var label: String
		public var position: Lane.Position

		public init(
			alleyId: Alley.ID,
			id: Lane.ID,
			label: String,
			position: Lane.Position
		) {
			self.alleyId = alleyId
			self.id = id
			self.label = label
			self.position = position
		}
	}
}

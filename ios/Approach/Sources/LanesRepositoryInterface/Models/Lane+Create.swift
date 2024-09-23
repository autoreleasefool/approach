import ModelsLibrary

extension Lane {
	public struct Create: Identifiable, Codable, Equatable, Sendable {
		public let alleyId: Alley.ID
		public let id: Lane.ID
		public var label: String
		public var position: Lane.Position

		public static func `default`(withId: Lane.ID, forAlley: Alley.ID) -> Self {
			.init(alleyId: forAlley, id: withId, label: "", position: .noWall)
		}

		public init(alleyId: Alley.ID, id: Lane.ID, label: String, position: Lane.Position) {
			self.alleyId = alleyId
			self.id = id
			self.label = label
			self.position = position
		}
	}
}

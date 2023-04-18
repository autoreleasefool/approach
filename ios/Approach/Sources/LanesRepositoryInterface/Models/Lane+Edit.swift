import ModelsLibrary

extension Lane {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Lane.ID
		public var label: String
		public var position: Lane.Position
	}
}

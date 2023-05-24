import ModelsLibrary

extension Bowler {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String
	}
}

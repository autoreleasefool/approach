import ModelsLibrary

extension Bowler {
	public struct Edit: Identifiable, Codable, Equatable, Sendable {
		public let id: Bowler.ID
		public var name: String

		public static let placeholder = Edit(id: Bowler.ID(), name: "")
	}
}

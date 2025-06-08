import Foundation

public enum Team {}

extension Team {
	public typealias ID = UUID
}

extension Team {
	public struct List: Identifiable, Codable, Hashable, Sendable {
		public let id: Team.ID
		public let name: String
		public let bowlers: [TeamMember]

		public init(
			id: Team.ID,
			name: String,
			bowlers: [TeamMember]
		) {
			self.id = id
			self.name = name
			self.bowlers = bowlers
		}
	}
}

extension Team.List {
	public struct TeamMember: Codable, Hashable, Sendable {
		public let name: String

		public init(name: String) {
			self.name = name
		}
	}
}

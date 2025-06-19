import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Team {
	public struct Edit: Identifiable, Codable, Equatable, Sendable {
		public let id: Team.ID
		public var name: String
	}
}

extension Team {
	public struct EditWithMembers: Equatable, Codable, Sendable {
		public var team: Edit
		public let members: IdentifiedArrayOf<Bowler.Summary>
	}
}

import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Team {
	public struct Create: Identifiable, Codable, Equatable, Sendable {
		public let id: Team.ID
		public var name: String

		public static func defaultTeam(withId: UUID) -> Self {
			.init(id: withId, name: "")
		}
	}
}

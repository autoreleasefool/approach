import Foundation
import ModelsLibrary

extension Bowler {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String
		public let status: Bowler.Status

		public static func defaultBowler(withId: UUID) -> Self {
			.init(id: withId, name: "", status: .playable)
		}

		public static func defaultOpponent(withId: UUID) -> Self {
			.init(id: withId, name: "", status: .opponent)
		}
	}
}

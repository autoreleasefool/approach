import Foundation
import ModelsLibrary

extension Bowler {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String
		public let kind: Bowler.Kind

		public static func defaultBowler(withId: UUID) -> Self {
			.init(id: withId, name: "", kind: .playable)
		}

		public static func defaultOpponent(withId: UUID) -> Self {
			.init(id: withId, name: "", kind: .opponent)
		}
	}
}

import Foundation
import ModelsLibrary

extension Gear {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var owner: Bowler.Summary?

		public static func `default`(withId: UUID) -> Self {
			.init(id: withId, name: "", kind: .bowlingBall, owner: nil)
		}
	}
}

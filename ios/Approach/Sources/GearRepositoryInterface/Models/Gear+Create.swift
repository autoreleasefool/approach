import Foundation
import ModelsLibrary

extension Gear {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var owner: Bowler.Summary?

		public static let `default` = Self(id: UUID(), name: "", kind: .bowlingBall, owner: nil)
	}
}

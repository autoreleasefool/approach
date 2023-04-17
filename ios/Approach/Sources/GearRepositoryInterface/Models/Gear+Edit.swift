import Foundation
import ModelsLibrary

extension Gear {
	public struct Edit: Identifiable, Equatable, Codable {
		public let id: Gear.ID
		public var name: String
		public let kind: Gear.Kind
		public var owner: Bowler.Summary?

		public static let `default` = Self(id: UUID(), name: "", kind: .bowlingBall, owner: nil)
	}
}

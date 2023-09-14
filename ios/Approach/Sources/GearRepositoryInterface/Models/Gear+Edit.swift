import Foundation
import ModelsLibrary

extension Gear {
	public struct Edit: Identifiable, Equatable, Codable {
		public let id: Gear.ID
		public let kind: Gear.Kind

		public var name: String
		public var owner: Bowler.Summary?
		public var avatar: Avatar.Summary?
	}
}

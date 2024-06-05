import Foundation

public enum Gear {}

extension Gear {
	public typealias ID = UUID
}

extension Gear {
	public enum Kind: String, Codable, Sendable, Identifiable, CaseIterable {
		case shoes
		case bowlingBall
		case towel
		case other

		public var id: String { rawValue }
	}
}

extension Gear {
	public struct Summary: Sendable, Identifiable, Codable, Hashable {
		public let id: Gear.ID
		public let name: String
		public let kind: Kind
		public let ownerName: String?
		public let avatar: Avatar.Summary

		public init(id: Gear.ID, name: String, kind: Kind, ownerName: String?, avatar: Avatar.Summary) {
			self.id = id
			self.name = name
			self.kind = kind
			self.ownerName = ownerName
			self.avatar = avatar
		}
	}
}

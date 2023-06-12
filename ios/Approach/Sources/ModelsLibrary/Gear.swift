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
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public let name: String
		public let kind: Kind
		public let ownerName: String?

		public var rolled: Rolled {
			.init(id: id, name: name)
		}
	}
}

extension Gear {
	public struct Named: Identifiable, Codable, Equatable, Sendable {
		public let id: Gear.ID
		public let name: String
	}
}

extension Gear {
	@available(*, deprecated)
	public typealias Rolled = Named
}

import Foundation

public enum Bowler {}

extension Bowler {
	public typealias ID = UUID
}

extension Bowler {
	public enum Status: String, Codable, Sendable {
		case playable
		case opponent
	}
}

extension Bowler {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public let name: String

		public init(id: Bowler.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

extension Bowler {
	public struct List: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public let name: String
		public let average: Double?

		public var summary: Summary {
			.init(id: id, name: name)
		}
	}
}

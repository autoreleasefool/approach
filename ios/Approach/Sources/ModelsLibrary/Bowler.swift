import Foundation
import IdentifiedCollections

public enum Bowler {}

extension Bowler {
	public typealias ID = UUID
}

extension Bowler {
	public enum Kind: String, Codable, Sendable {
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

extension Bowler {
	public struct OpponentDetails: Identifiable, Decodable, Equatable {
		public let id: Bowler.ID
		public let name: String
		public let matchesAgainst: IdentifiedArrayOf<Game.ListMatch>
		public let gamesPlayed: Int
		public let gamesWon: Int
		public let gamesLost: Int
		public let gamesTied: Int
	}
}

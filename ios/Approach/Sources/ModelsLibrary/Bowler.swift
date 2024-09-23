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
	public struct Summary: Identifiable, Codable, Hashable, Sendable {
		public let id: Bowler.ID
		public let name: String

		public init(id: Bowler.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

extension Bowler {
	public struct Opponent: Identifiable, Codable, Equatable, Sendable {
		public let id: Bowler.ID
		public let name: String
		public let kind: Kind

		public init(id: Bowler.ID, name: String, kind: Kind) {
			self.id = id
			self.name = name
			self.kind = kind
		}

		public var summary: Summary { .init(id: id, name: name) }
	}
}

extension Bowler {
	public struct List: Identifiable, Codable, Equatable, Sendable {
		public let id: Bowler.ID
		public let name: String
		public let average: Double?

		public var summary: Summary {
			.init(id: id, name: name)
		}
	}
}

extension Bowler {
	public struct Archived: Identifiable, Codable, Equatable, Sendable {
		public let id: Bowler.ID
		public let name: String
		public let totalNumberOfLeagues: Int
		public let totalNumberOfSeries: Int
		public let totalNumberOfGames: Int
		public let archivedOn: Date?
	}
}

extension Bowler {
	public struct OpponentDetails: Identifiable, Decodable, Equatable, Sendable {
		public let id: Bowler.ID
		public let name: String
		public let matchesAgainst: IdentifiedArrayOf<Game.ListMatch>
		public let gamesPlayed: Int
		public let gamesWon: Int
		public let gamesLost: Int
		public let gamesTied: Int

		public static let placeholder = OpponentDetails(
			id: Bowler.ID(),
			name: "",
			matchesAgainst: [],
			gamesPlayed: 0,
			gamesWon: 0,
			gamesLost: 0,
			gamesTied: 0
		)
	}
}

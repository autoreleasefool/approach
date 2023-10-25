import Foundation

public enum League {
	public static let DEFAULT_NUMBER_OF_GAMES = 4
	public static let NUMBER_OF_GAMES_RANGE = 1...40
}

extension League {
	public typealias ID = UUID
}

extension League {
	public enum Recurrence: String, Codable, Sendable, Identifiable, CaseIterable {
		case repeating
		case once

		public var id: String { rawValue }
	}
}

extension League {
	public enum ExcludeFromStatistics: String, Codable, Sendable, Identifiable, CaseIterable {
		case include
		case exclude

		public var id: String { rawValue }
	}
}

extension League {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let name: String

		public init(id: League.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

extension League {
	public struct Archived: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let name: String
		public let bowlerName: String
		public let totalNumberOfSeries: Int
		public let totalNumberOfGames: Int
		public let archivedOn: Date?
	}
}

extension League {
	public struct SeriesHost: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let name: String
		public let numberOfGames: Int?
		public let alley: Alley.Summary?
		public let excludeFromStatistics: League.ExcludeFromStatistics

		public init(
			id: League.ID,
			name: String,
			numberOfGames: Int?,
			alley: Alley.Summary?,
			excludeFromStatistics: League.ExcludeFromStatistics
		) {
			self.id = id
			self.name = name
			self.numberOfGames = numberOfGames
			self.alley = alley
			self.excludeFromStatistics = excludeFromStatistics
		}
	}
}

extension League {
	public struct List: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let name: String
		public let average: Double?

		public var summary: Summary {
			.init(id: id, name: name)
		}
	}
}

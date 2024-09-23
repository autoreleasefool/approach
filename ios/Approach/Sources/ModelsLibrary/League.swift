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
	public struct Summary: Identifiable, Codable, Equatable, Sendable {
		public let id: League.ID
		public let name: String

		public init(id: League.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

extension League {
	public struct Archived: Identifiable, Codable, Equatable, Sendable {
		public let id: League.ID
		public let name: String
		public let bowlerName: String
		public let totalNumberOfSeries: Int
		public let totalNumberOfGames: Int
		public let archivedOn: Date?
	}
}

extension League {
	public struct SeriesHost: Identifiable, Codable, Equatable, Sendable {
		public let id: League.ID
		public let name: String
		public let defaultNumberOfGames: Int?
		public let alley: Alley.Summary?
		public let excludeFromStatistics: League.ExcludeFromStatistics
		public let recurrence: League.Recurrence

		public init(
			id: League.ID,
			name: String,
			defaultNumberOfGames: Int?,
			alley: Alley.Summary?,
			excludeFromStatistics: League.ExcludeFromStatistics,
			recurrence: League.Recurrence
		) {
			self.id = id
			self.name = name
			self.defaultNumberOfGames = defaultNumberOfGames
			self.alley = alley
			self.excludeFromStatistics = excludeFromStatistics
			self.recurrence = recurrence
		}

		public static let placeholder = SeriesHost(
			id: League.ID(),
			name: "",
			defaultNumberOfGames: nil,
			alley: nil,
			excludeFromStatistics: .include,
			recurrence: .repeating
		)
	}
}

extension League {
	public struct List: Identifiable, Codable, Equatable, Sendable {
		public let id: League.ID
		public let name: String
		public let recurrence: Recurrence
		public let average: Double?

		public var summary: Summary {
			.init(id: id, name: name)
		}
	}
}

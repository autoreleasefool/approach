import Foundation

public enum Series {}

// MARK: - Properties

extension Series {
	public typealias ID = UUID
}

extension Series {
	public enum PreBowl: String, Codable, Sendable, Identifiable, CaseIterable {
		case regular
		case preBowl

		public var id: String { rawValue }
	}
}

extension Series {
	public enum ExcludeFromStatistics: String, Codable, Sendable, Identifiable, CaseIterable {
		case include
		case exclude

		public var id: String { rawValue }

		public init(from: League.ExcludeFromStatistics) {
			switch from {
			case .include:
				self = .include
			case .exclude:
				self = .exclude
			}
		}
	}
}

// MARK: - Models

extension Series {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Series.ID
		public let date: Date

		public init(id: Series.ID, date: Date) {
			self.id = id
			self.date = date
		}
	}
}

extension Series {
	public struct GameHost: Identifiable, Codable, Equatable, CanPreBowl {
		public let id: Series.ID
		public let date: Date
		public let appliedDate: Date?
		public let preBowl: Series.PreBowl

		public init(id: Series.ID, date: Date, appliedDate: Date?, preBowl: Series.PreBowl) {
			self.id = id
			self.date = date
			self.appliedDate = appliedDate
			self.preBowl = preBowl
		}
	}
}

extension Series {
	public struct List: Identifiable, Codable, Equatable, CanPreBowl {
		public let id: Series.ID
		public let date: Date
		public let appliedDate: Date?
		public let scores: [Series.List.Score]
		public let total: Int
		public let preBowl: Series.PreBowl

		public init(
			id: Series.ID,
			date: Date,
			appliedDate: Date?,
			scores: [Series.List.Score],
			total: Int,
			preBowl: Series.PreBowl
		) {
			self.id = id
			self.date = date
			self.appliedDate = appliedDate
			self.scores = scores
			self.total = total
			self.preBowl = preBowl
		}

		public var asSummary: Summary {
			.init(id: id, date: date)
		}

		public var asGameHost: GameHost {
			.init(id: id, date: date, appliedDate: appliedDate, preBowl: preBowl)
		}
	}
}

extension Series.List {
	public struct Score: Identifiable, Equatable, Codable {
		public let index: Int
		public let score: Int

		public var id: Int { index }

		public init(index: Int, score: Int) {
			self.index = index
			self.score = score
		}
	}
}

extension Series {
	public struct Archived: Identifiable, Codable, Equatable {
		public let id: Series.ID
		public let date: Date
		public let bowlerName: String
		public let leagueName: String
		public let totalNumberOfGames: Int
		public let archivedOn: Date?
	}
}

extension Series {
	public struct Shareable: Identifiable, Codable, Equatable {
		public let id: Series.ID
		public let date: Date
		public let bowlerName: String
		public let leagueName: String
		public let total: Int
		public let scores: [Series.Shareable.Score]

		public init(
			id: Series.ID,
			date: Date,
			bowlerName: String,
			leagueName: String,
			total: Int,
			scores: [Series.Shareable.Score]
		) {
			self.id = id
			self.date = date
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.total = total
			self.scores = scores
		}
	}
}

extension Series.Shareable {
	public struct Score: Identifiable, Equatable, Codable {
		public let index: Int
		public let score: Int

		public var id: Int { index }

		public init(index: Int, score: Int) {
			self.index = index
			self.score = score
		}
	}
}

// MARK: - Protocols

public protocol CanPreBowl {
	var date: Date { get }
	var appliedDate: Date? { get }
	var preBowl: Series.PreBowl { get }
}

extension CanPreBowl {
	public var primaryDate: Date {
		appliedDate ?? date
	}

	public var bowledOnDate: Date? {
		appliedDate == nil ? nil : date
	}

	public var isPreBowlUsed: Bool {
		preBowl == .preBowl && appliedDate != nil
	}
}

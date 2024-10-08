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
	public struct Summary: Identifiable, Codable, Equatable, Sendable {
		public let id: Series.ID
		public let date: Date

		public init(id: Series.ID, date: Date) {
			self.id = id
			self.date = date
		}
	}
}

extension Series {
	public struct GameHost: Identifiable, Codable, Equatable, Sendable, CanPreBowl {
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

		public static let placeholder = GameHost(id: Series.ID(), date: Date(), appliedDate: nil, preBowl: .regular)
	}
}

extension Series {
	public struct List: Identifiable, Codable, Equatable, Sendable, CanPreBowl {
		public let id: Series.ID
		public let date: Date
		public let appliedDate: Date?
		public let scores: [Game.Score]
		public let total: Int
		public let preBowl: Series.PreBowl

		public init(
			id: Series.ID,
			date: Date,
			appliedDate: Date?,
			scores: [Game.Score],
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

extension Series {
	public struct Archived: Identifiable, Codable, Equatable, Sendable {
		public let id: Series.ID
		public let date: Date
		public let bowlerName: String
		public let leagueName: String
		public let totalNumberOfGames: Int
		public let archivedOn: Date?
	}
}

extension Series {
	public struct Shareable: Identifiable, Codable, Equatable, Sendable {
		public let id: Series.ID
		public let date: Date
		public let bowlerName: String
		public let leagueName: String
		public let total: Int
		public let scores: [Game.Score]

		public init(
			id: Series.ID,
			date: Date,
			bowlerName: String,
			leagueName: String,
			total: Int,
			scores: [Game.Score]
		) {
			self.id = id
			self.date = date
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.total = total
			self.scores = scores
		}

		public static let placeholder = Shareable(
			id: Series.ID(),
			date: Date(),
			bowlerName: "",
			leagueName: "",
			total: 0,
			scores: []
		)
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

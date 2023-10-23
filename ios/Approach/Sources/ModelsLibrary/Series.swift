import Foundation

public enum Series {}

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

extension Series {
	public enum ArchiveState: String, Codable, Sendable {
		case available
		case archived
	}
}

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
	public struct List: Identifiable, Codable, Equatable {
		public let id: Series.ID
		public let date: Date
		public let scores: [Series.List.Score]
		public let total: Int
		public let preBowl: Series.PreBowl

		public init(id: Series.ID, date: Date, scores: [Series.List.Score], total: Int, preBowl: Series.PreBowl) {
			self.id = id
			self.date = date
			self.scores = scores
			self.total = total
			self.preBowl = preBowl
		}

		public var asSummary: Summary {
			.init(id: id, date: date)
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

		public init(
			id: Series.ID,
			date: Date,
			bowlerName: String,
			leagueName: String,
			totalNumberOfGames: Int
		) {
			self.id = id
			self.date = date
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.totalNumberOfGames = totalNumberOfGames
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

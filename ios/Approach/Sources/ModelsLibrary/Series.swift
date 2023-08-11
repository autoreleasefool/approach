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
		public let scores: [Int]
		public let total: Int
		public let preBowl: Series.PreBowl

		public init(id: Series.ID, date: Date, scores: [Int], total: Int, preBowl: Series.PreBowl) {
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

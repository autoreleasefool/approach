import Foundation

public enum Game {}

extension Game {
	public typealias ID = UUID

	public static let NUMBER_OF_FRAMES = 10
	public static let FRAME_INDICES = 0..<NUMBER_OF_FRAMES
	public static let MAXIMUM_SCORE = 450

	public static func frameIndices(after: Int, upTo: Int = NUMBER_OF_FRAMES) -> Range<Int> {
		(after + 1)..<upTo
	}
}

extension Game {
	public enum Lock: String, Codable, Sendable, Identifiable, CaseIterable {
		case locked
		case open

		public var id: String { rawValue }
	}
}

extension Game {
	public enum ScoringMethod: String, Codable, Sendable, CaseIterable {
		case manual
		case byFrame
	}
}

extension Game {
	public enum ExcludeFromStatistics: String, Codable, Sendable, Identifiable, CaseIterable {
		case include
		case exclude

		public var id: String { rawValue }

		public init(from: Series.ExcludeFromStatistics) {
			switch from {
			case .include:
				self = .include
			case .exclude:
				self = .exclude
			}
		}
	}
}

extension Game {
	public struct Summary: Identifiable, Equatable, Codable {
		public let id: Game.ID
		public let index: Int
		public let score: Int
	}
}

extension Game {
	public struct List: Identifiable, Equatable, Codable {
		public let id: Game.ID
		public let bowlerId: Bowler.ID
		public let index: Int
		public let score: Int
	}
}

extension Game {
	public struct ListMatch: Identifiable, Equatable, Codable {
		public let id: Game.ID
		public let score: Int
		public let opponentScore: Int?
		public let result: MatchPlay.Result?
	}
}

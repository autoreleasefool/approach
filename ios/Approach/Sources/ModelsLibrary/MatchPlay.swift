import Foundation

public enum MatchPlay {}

extension MatchPlay {
	public typealias ID = UUID
}

extension MatchPlay {
	public enum Result: String, Codable, Sendable, Identifiable, CaseIterable {
		case tied
		case won
		case lost

		public var id: String { rawValue }
	}
}

extension MatchPlay {
	public struct Summary: Identifiable, Codable, Equatable, Sendable {
		public let gameId: Game.ID
		public let id: MatchPlay.ID
		public let opponent: Bowler.Summary
		public let opponentScore: Int?
		public let result: MatchPlay.Result?
	}
}

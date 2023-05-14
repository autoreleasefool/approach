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

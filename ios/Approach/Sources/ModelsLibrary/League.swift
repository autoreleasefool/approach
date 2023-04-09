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

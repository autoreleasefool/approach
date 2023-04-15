import Foundation

public enum Game {}

extension Game {
	public typealias ID = UUID
}

extension Game {
	public enum Lock: String, Codable, Sendable, Identifiable, CaseIterable {
		case locked
		case open

		public var id: String { rawValue }
	}
}

extension Game {
	public enum ExcludeFromStatistics: String, Codable, Sendable, Identifiable, CaseIterable {
		case include
		case exclude

		public var id: String { rawValue }
	}
}

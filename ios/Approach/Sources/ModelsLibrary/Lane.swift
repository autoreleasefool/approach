import Foundation

public enum Lane {}

extension Lane {
	public typealias ID = UUID
}

extension Lane {
	public enum Position: String, Codable, Sendable {
		case leftWall
		case rightWall
		case noWall

		public var id: String { rawValue }
	}
}

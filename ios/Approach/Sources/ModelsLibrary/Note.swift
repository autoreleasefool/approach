import Foundation

public enum Note {}

extension Note {
	public typealias ID = UUID
}

// MARK: Entity

extension Note {
	public enum EntityType: String, Codable, Sendable {
		case bowler
		case league
		case series
		case game
		case gear
		case alley
	}
}

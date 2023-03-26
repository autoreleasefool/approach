import Foundation

public enum Bowler {}

extension Bowler {
	public typealias ID = UUID
}

extension Bowler {
	public enum Status: String, Codable, Sendable {
		case playable
		case opponent
	}
}

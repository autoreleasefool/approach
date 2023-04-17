import Foundation

public enum Gear {}

extension Gear {
	public typealias ID = UUID
}

extension Gear {
	public enum Kind: String, Codable, Sendable, Identifiable, CaseIterable {
		case shoes
		case bowlingBall
		case towel
		case other

		public var id: String { rawValue }
	}
}

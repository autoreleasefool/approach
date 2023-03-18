import Foundation

public struct Frame: Sendable, Identifiable, Hashable, Codable {
	public let game: Game.ID
	public let ordinal: Int
	public var rolls: [Roll]

	public var id: String {
		"\(game)-\(ordinal)"
	}

	public init(
		game: Game.ID,
		ordinal: Int,
		rolls: [Roll]
	) {
		self.game = game
		self.ordinal = ordinal
		self.rolls = rolls
	}
}

// MARK: - Roll

extension Frame {
	public struct Roll: Sendable, Hashable, Codable {
		public var pinsDowned: [Pin]
		public var didFoul: Bool

		public init(pinsDowned: [Pin], didFoul: Bool) {
			self.pinsDowned = pinsDowned
			self.didFoul = didFoul
		}

		public static let `default`: Self = .init(pinsDowned: [], didFoul: false)
	}
}

// MARK: - Pin

public enum Pin: Int, Equatable, Sendable, Identifiable, Codable {
	case leftTwoPin = 0
	case leftThreePin = 1
	case headPin = 2
	case rightThreePin = 3
	case rightTwoPin = 4

	public var id: Int { rawValue }

	public static let fullDeck: [Self] = [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin]
}

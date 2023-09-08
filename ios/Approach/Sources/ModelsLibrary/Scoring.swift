public struct ScoredGame: Sendable, Identifiable, Equatable {
	public let id: Game.ID
	public let index: Int
	public let frames: [ScoredFrame]

	public init(id: Game.ID, index: Int, frames: [ScoredFrame]) {
		self.id = id
		self.index = index
		self.frames = frames
	}

	public var score: Int? { frames.gameScore() }
}

public struct ScoredFrame: Sendable, Equatable {
	public let index: Int
	public let rolls: [ScoredRoll]
	public let score: Int?

	public var displayValue: String? {
		if let score {
			return String(score)
		} else {
			return nil
		}
	}

	public init(index: Int, rolls: [ScoredRoll], score: Int?) {
		self.index = index
		self.rolls = rolls
		self.score = score
	}
}

public struct ScoredRoll: Sendable, Equatable {
	public let index: Int
	public let displayValue: String?
	public let didFoul: Bool

	public init(index: Int, displayValue: String?, didFoul: Bool) {
		self.index = index
		self.displayValue = displayValue
		self.didFoul = didFoul
	}
}

extension Collection where Element == ScoredFrame {
	public func gameScore() -> Int? {
		reversed().first(where: { $0.score != nil })?.score
	}
}

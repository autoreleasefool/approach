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

extension Collection where Element == ScoredFrame {
	public func gameScore() -> Int? {
		reversed().first(where: { $0.score != nil })?.score
	}
}

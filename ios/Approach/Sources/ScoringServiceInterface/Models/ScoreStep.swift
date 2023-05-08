/// Each ScoreStep represents a single frame's scored value within a game
public struct ScoreStep: Sendable, Equatable {
	public let index: Int
	/// The individual values for each roll in this step
	public let rolls: [RollStep]
	/// The score after this step in the game
	public let score: Int?

	public var display: String? {
		if let score {
			return String(score)
		} else {
			return nil
		}
	}

	public init(index: Int, rolls: [RollStep], score: Int?) {
		self.index = index
		self.rolls = rolls
		self.score = score
	}
}

extension ScoreStep {
	public struct RollStep: Sendable, Equatable {
		public let index: Int
		/// The display value of the roll, e.g. X, /, A, R, L, S, Hp, H2, 2, 3, etc
		public let display: String?
		/// If a foul was incurred for this roll
		public let didFoul: Bool

		public init(index: Int, display: String?, didFoul: Bool) {
			self.index = index
			self.display = display
			self.didFoul = didFoul
		}
	}
}

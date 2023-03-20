/// Each ScoreStep represents a single frame's scored value within a game
public struct ScoreStep: Sendable {
	/// The individual values for each roll in this step
	public let rolls: [RollStep]
	/// The score after this step in the game
	public let score: Int?

	public init(rolls: [RollStep], score: Int?) {
		self.rolls = rolls
		self.score = score
	}
}

extension ScoreStep {
	public struct RollStep: Sendable {
		/// The display value of the roll, e.g. X, /, A, R, L, S, Hp, H2, 2, 3, etc
		public let display: String
		/// If a foul was incurred for this roll
		public let didFoul: Bool

		public init(display: String, didFoul: Bool) {
			self.display = display
			self.didFoul = didFoul
		}
	}
}

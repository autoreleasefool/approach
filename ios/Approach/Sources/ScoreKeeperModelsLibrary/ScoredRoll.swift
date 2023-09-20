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

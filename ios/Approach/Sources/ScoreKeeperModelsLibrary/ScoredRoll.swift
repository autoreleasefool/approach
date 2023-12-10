public struct ScoredRoll: Sendable, Equatable {
	public let index: Int
	public let displayValue: String?
	public let didFoul: Bool
	public let isSecondary: Bool

	public init(index: Int, displayValue: String?, didFoul: Bool, isSecondary: Bool) {
		self.index = index
		self.displayValue = displayValue
		self.didFoul = didFoul
		self.isSecondary = isSecondary
	}
}

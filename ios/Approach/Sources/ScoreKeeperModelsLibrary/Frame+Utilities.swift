public enum Frame {}

extension Frame {
	public static let NUMBER_OF_ROLLS = 3
	public static let ROLL_INDICES = 0..<NUMBER_OF_ROLLS

	public static func isLast(_ index: Int) -> Bool {
		index == Game.NUMBER_OF_FRAMES - 1
	}

	public static func rollIndices(after: Int) -> Range<Int> {
		(after + 1)..<NUMBER_OF_ROLLS
	}

	public static func isLastRoll(_ index: Int) -> Bool {
		index == Frame.NUMBER_OF_ROLLS - 1
	}
}

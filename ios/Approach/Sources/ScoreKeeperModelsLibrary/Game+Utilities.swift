public enum Game {}

extension Game {
	public static let NUMBER_OF_FRAMES = 10
	public static let FRAME_INDICES = 0..<NUMBER_OF_FRAMES
	public static let FOUL_PENALTY = 15
	public static let MAXIMUM_SCORE = 450

	public static func frameIndices(after: Int, upTo: Int = NUMBER_OF_FRAMES) -> Range<Int> {
		(after + 1)..<upTo
	}
}

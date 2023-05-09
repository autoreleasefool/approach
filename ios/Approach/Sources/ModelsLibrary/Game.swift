import Foundation

public enum Game {}

extension Game {
	public typealias ID = UUID

	public static let NUMBER_OF_FRAMES = 10
	public static let FRAME_INDICES = 0..<NUMBER_OF_FRAMES

	public static func frameIndices(after: Int) -> Range<Int> {
		(after + 1)..<NUMBER_OF_FRAMES
	}
}

extension Game {
	public enum Lock: String, Codable, Sendable, Identifiable, CaseIterable {
		case locked
		case open

		public var id: String { rawValue }
	}
}

extension Game {
	public enum ExcludeFromStatistics: String, Codable, Sendable, Identifiable, CaseIterable {
		case include
		case exclude

		public var id: String { rawValue }

		public init(from: Series.ExcludeFromStatistics) {
			switch from {
			case .include:
				self = .include
			case .exclude:
				self = .exclude
			}
		}
	}
}

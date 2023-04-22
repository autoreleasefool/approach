import Foundation

public enum Alley {}

extension Alley {
	public typealias ID = UUID

	public static let NUMBER_OF_LANES_RANGE = 0...1000
}

extension Alley {
	public enum Material: String, Codable, Sendable, Identifiable, CaseIterable {
		case synthetic
		case wood

		public var id: String { rawValue }
	}
}

extension Alley {
	public enum PinFall: String, Codable, Sendable, Identifiable, CaseIterable {
		case freefall
		case strings

		public var id: String { rawValue }
	}
}

extension Alley {
	public enum Mechanism: String, Codable, Sendable, Identifiable, CaseIterable {
		case dedicated
		case interchangeable

		public var id: String { rawValue }
	}
}

extension Alley {
	public enum PinBase: String, Codable, Sendable, Identifiable, CaseIterable {
		case black
		case white
		case other

		public var id: String { rawValue }
	}
}

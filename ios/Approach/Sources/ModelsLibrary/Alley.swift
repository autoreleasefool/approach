import Foundation

public enum Alley {}

extension Alley {
	public typealias ID = UUID
}

extension Alley {
	public enum Material: String, Codable, Sendable, CaseIterable {
		case synthetic
		case wood
	}
}

extension Alley {
	public enum PinFall: String, Codable, Sendable, CaseIterable {
		case freefall
		case strings
	}
}

extension Alley {
	public enum Mechanism: String, Codable, Sendable, CaseIterable {
		case dedicated
		case interchangeable
	}
}

extension Alley {
	public enum PinBase: String, Codable, Sendable, CaseIterable {
		case black
		case white
		case other
	}
}

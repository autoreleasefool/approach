import Foundation

public enum Series {}

extension Series {
	public typealias ID = UUID
}

extension Series {
	public enum PreBowl: String, Codable, Sendable, Identifiable, CaseIterable {
		case regular
		case preBowl

		public var id: String { rawValue }
	}
}

extension Series {
	public enum ExcludeFromStatistics: String, Codable, Sendable, Identifiable, CaseIterable {
		case include
		case exclude

		public var id: String { rawValue }

		public init(from: League.ExcludeFromStatistics) {
			switch from {
			case .include:
				self = .include
			case .exclude:
				self = .exclude
			}
		}
	}
}

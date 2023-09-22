import Foundation

public enum Lane {}

extension Lane {
	public typealias ID = UUID
}

extension Lane {
	public enum Position: String, Codable, Sendable, CaseIterable, Identifiable {
		case leftWall
		case rightWall
		case noWall

		public var id: String { rawValue }
	}
}

extension Lane {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Lane.ID
		public let label: String
		public let position: Lane.Position

		public init(id: Lane.ID, label: String, position: Lane.Position) {
			self.id = id
			self.label = label
			self.position = position
		}
	}
}

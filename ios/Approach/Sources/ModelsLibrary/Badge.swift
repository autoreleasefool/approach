import Foundation

public enum Achievement {}

extension Achievement {
	public typealias ID = UUID
}

// MARK: Summary

extension Achievement {
	public struct Summary: Identifiable, Codable, Hashable, Sendable {
		public let id: Achievement.ID
		public let name: String
		public let earnedAt: Date

		public init(id: Achievement.ID, name: String, earnedAt: Date) {
			self.id = id
			self.name = name
			self.earnedAt = earnedAt
		}
	}
}

// MARK: Event

extension Achievement {
	public struct Event: Identifiable, Codable, Hashable, Sendable {
		public let id: Achievement.ID
		public let name: String
		public let isConsumed: Bool

		public init(id: Achievement.ID, name: String, isConsumed: Bool) {
			self.id = id
			self.name = name
			self.isConsumed = isConsumed
		}
	}
}

import Foundation

public enum Achievement {}

extension Achievement {
	public typealias ID = UUID
}

// MARK: Summary

extension Achievement {
	public struct Summary: Identifiable, Codable, Hashable, Sendable {
		public let id: Achievement.ID
		public let title: String
		public let earnedAt: Date

		public init(id: Achievement.ID, title: String, earnedAt: Date) {
			self.id = id
			self.title = title
			self.earnedAt = earnedAt
		}
	}
}

// MARK: Event

extension Achievement {
	public struct Event: Identifiable, Codable, Hashable, Sendable {
		public let id: Achievement.ID
		public let title: String
		public let isConsumed: Bool

		public init(id: Achievement.ID, title: String, isConsumed: Bool) {
			self.id = id
			self.title = title
			self.isConsumed = isConsumed
		}
	}
}

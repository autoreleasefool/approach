import Foundation

public enum Badge {}

extension Badge {
	public typealias ID = UUID
}

// MARK: Summary

extension Badge {
	public struct Summary: Identifiable, Codable, Hashable, Sendable {
		public let id: Badge.ID
		public let name: String
		public let earnedAt: Date

		public init(id: Badge.ID, name: String, earnedAt: Date) {
			self.id = id
			self.name = name
			self.earnedAt = earnedAt
		}
	}
}

// MARK: Event

extension Badge {
	public struct Event: Identifiable, Codable, Hashable, Sendable {
		public let id: Badge.ID
		public let name: String
		public let isConsumed: Bool

		public init(id: Badge.ID, name: String, isConsumed: Bool) {
			self.id = id
			self.name = name
			self.isConsumed = isConsumed
		}
	}
}

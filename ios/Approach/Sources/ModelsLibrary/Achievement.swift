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

extension Achievement {
	public struct Counted: Identifiable, Codable, Hashable, Sendable {
		public let title: String
		public let firstEarnedAt: Date?
		public let count: Int

		public var id: String { title }

		public init(title: String, firstEarnedAt: Date?, count: Int) {
			self.title = title
			self.firstEarnedAt = firstEarnedAt
			self.count = count
		}
	}
}

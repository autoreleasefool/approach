import Foundation

public enum AchievementEvent {}

extension AchievementEvent {
	public typealias ID = UUID
}

// MARK: Summary

extension AchievementEvent {
	public struct Summary: Identifiable, Codable, Hashable, Sendable {
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

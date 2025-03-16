import Foundation
import GRDB
import ModelsLibrary

extension Achievement {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Achievement.ID
		public var name: String
		public var earnedAt: Date

		public init(id: Achievement.ID, name: String, earnedAt: Date) {
			self.id = id
			self.name = name
			self.earnedAt = earnedAt
		}
	}
}

extension Achievement.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "achievement"
}

extension Achievement.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let earnedAt = Column(CodingKeys.earnedAt)
	}
}

extension Achievement.Summary: FetchableRecord {}

// MARK: - Events

extension Achievement.Event {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Achievement.Event.ID
		public var title: String
		public var isConsumed: Bool

		public init(id: Achievement.Event.ID, title: String, isConsumed: Bool) {
			self.id = id
			self.title = title
			self.isConsumed = isConsumed
		}
	}
}

extension Achievement.Event.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "achievementEvent"
}

extension Achievement.Event.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let title = Column(CodingKeys.title)
		public static let isConsumed = Column(CodingKeys.isConsumed)
	}
}

extension Achievement.Event: FetchableRecord {}

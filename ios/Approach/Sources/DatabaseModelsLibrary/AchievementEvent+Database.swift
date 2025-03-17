import Foundation
import GRDB
import ModelsLibrary

extension AchievementEvent {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: AchievementEvent.ID
		public var title: String
		public var isConsumed: Bool

		public init(id: AchievementEvent.ID, title: String, isConsumed: Bool) {
			self.id = id
			self.title = title
			self.isConsumed = isConsumed
		}
	}
}

extension AchievementEvent.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "achievementEvent"
}

extension AchievementEvent.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let title = Column(CodingKeys.title)
		public static let isConsumed = Column(CodingKeys.isConsumed)
	}
}

extension AchievementEvent.Summary: FetchableRecord {}

import Foundation
import GRDB
import ModelsLibrary

extension Achievement {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Achievement.ID
		public var title: String
		public var earnedAt: Date

		public init(id: Achievement.ID, title: String, earnedAt: Date) {
			self.id = id
			self.title = title
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
		public static let title = Column(CodingKeys.title)
		public static let earnedAt = Column(CodingKeys.earnedAt)
	}
}

extension Achievement.Summary: FetchableRecord {}

extension Achievement.Counted: FetchableRecord {}

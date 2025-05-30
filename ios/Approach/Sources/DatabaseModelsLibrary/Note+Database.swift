import Foundation
import GRDB
import ModelsLibrary

extension Note {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Note.ID
		public var entityType: Note.EntityType
		public var entityId: UUID
		public var content: String

		public init(
			id: Note.ID,
			entityType: Note.EntityType,
			entityId: UUID,
			content: String
		) {
			self.id = id
			self.entityType = entityType
			self.entityId = entityId
			self.content = content
		}
	}
}

extension Note.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "note"
}

extension Note.EntityType: DatabaseValueConvertible {}

extension Note.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let entityType = Column(CodingKeys.entityType)
		public static let entityId = Column(CodingKeys.entityId)
		public static let content = Column(CodingKeys.content)
	}
}

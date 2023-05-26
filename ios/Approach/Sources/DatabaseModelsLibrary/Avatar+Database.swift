import Foundation
import GRDB
import ModelsLibrary

extension Avatar {
	public struct Database: Sendable, Identifiable, Codable, Equatable, TableRecord {
		public static let databaseTableName = "avatar"

		public let id: Avatar.ID
		public var value: Avatar.Value

		public init(id: Avatar.ID, value: Avatar.Value) {
			self.id = id
			self.value = value
		}
	}
}

extension Avatar.Database: FetchableRecord, PersistableRecord {}

extension Avatar.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let value = Column(CodingKeys.value)
	}
}

extension Avatar.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Avatar.Database.databaseTableName
}

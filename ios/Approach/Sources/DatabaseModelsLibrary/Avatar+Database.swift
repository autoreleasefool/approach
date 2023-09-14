import Foundation
import GRDB
import ModelsLibrary

extension Avatar {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Avatar.ID
		public var value: Avatar.Value

		public init(id: Avatar.ID, value: Avatar.Value) {
			self.id = id
			self.value = value
		}
	}
}

extension Avatar.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "avatar"
}

extension Avatar.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let value = Column(CodingKeys.value)
	}
}

extension Avatar.Summary: FetchableRecord {}

extension Avatar.Summary {
	public var databaseModel: Avatar.Database {
		.init(id: id, value: value)
	}
}

import Foundation
import GRDB
import ModelsLibrary

extension Team {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Team.ID
		public var name: String

		public init(id: Team.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

extension Team.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName: String = "team"
}

extension Team.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
	}
}

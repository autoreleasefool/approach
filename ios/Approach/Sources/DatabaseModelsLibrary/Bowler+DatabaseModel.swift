import ExtensionsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Bowler {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "bowler"

		public let id: Bowler.ID
		public var name: String
		public var status: Status

		public init(
			id: Bowler.ID,
			name: String,
			status: Status
		) {
			self.id = id
			self.name = name
			self.status = status
		}
	}
}

extension Bowler.Status: DatabaseValueConvertible {}

extension Bowler.Database: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw DBValidationError.usingPlaceholderId }
	}
}

extension Bowler.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let status = Column(CodingKeys.status)
	}
}

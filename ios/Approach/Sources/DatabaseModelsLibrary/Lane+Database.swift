import Foundation
import GRDB
import ModelsLibrary

extension Lane {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "lane"

		public let alleyId: Alley.ID
		public let id: Lane.ID
		public var label: String
		public var position: Lane.Position

		public init(
			alleyId: Alley.ID,
			id: Lane.ID,
			label: String,
			position: Lane.Position
		) {
			self.alleyId = alleyId
			self.id = id
			self.label = label
			self.position = position
		}
	}
}

extension Lane.Position: DatabaseValueConvertible {}

extension Lane.Database: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw PlaceholderIDValidationError() }
	}
}

extension Lane.Database {
	public enum Columns {
		public static let alleyId = Column(CodingKeys.alleyId)
		public static let id = Column(CodingKeys.id)
		public static let label = Column(CodingKeys.label)
		public static let position = Column(CodingKeys.position)
	}
}

extension Lane.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Lane.Database.databaseTableName
}

import Foundation
import GRDB
import ModelsLibrary

extension Lane {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
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

extension Lane.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "lane"
}

extension Lane.Position: DatabaseValueConvertible {}

extension DerivableRequest<Lane.Database> {
	public func orderByLabel() -> Self {
		// FIXME: Remove janky alphanumeric label ordering
		// Source: https://stackoverflow.com/a/5189719
		order(sql: "\(Lane.Database.Columns.label.name) * 1")
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

extension Lane.Summary: FetchableRecord {}

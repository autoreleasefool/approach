import Foundation
import GRDB
import ModelsLibrary

extension Location {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "location"

		public let id: Location.ID
		public var title: String
		public var latitude: Double
		public var longitude: Double

		public init(id: Location.ID, title: String, latitude: Double, longitude: Double) {
			self.id = id
			self.title = title
			self.latitude = latitude
			self.longitude = longitude
		}
	}
}

extension Location.Database: FetchableRecord, PersistableRecord {}

extension Location.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let title = Column(CodingKeys.title)
		public static let latitude = Column(CodingKeys.latitude)
		public static let longitude = Column(CodingKeys.longitude)
	}
}

extension Location.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Location.Database.databaseTableName
}

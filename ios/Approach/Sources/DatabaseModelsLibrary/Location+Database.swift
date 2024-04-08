import CloudKit
import Foundation
import GRDB
import Harmony
import ModelsLibrary

extension Location {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Location.ID
		public var title: String
		public var subtitle: String
		public var latitude: Double
		public var longitude: Double
		public var archivedRecordData: Data?

		public init(id: Location.ID, title: String, subtitle: String, latitude: Double, longitude: Double) {
			self.id = id
			self.title = title
			self.subtitle = subtitle
			self.latitude = latitude
			self.longitude = longitude
		}
	}
}

extension Location.Database: HRecord {
	public var zoneID: CKRecordZone.ID {
		.init(zoneName: Self.databaseTableName, ownerName: CKCurrentUserDefaultName)
	}

	public var record: CKRecord {
		let encoder = CKRecordEncoder(zoneID: zoneID)
		return try! encoder.encode(self)
	}
}

extension Location.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "location"
}

extension Location.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let title = Column(CodingKeys.title)
		public static let subtitle = Column(CodingKeys.subtitle)
		public static let latitude = Column(CodingKeys.latitude)
		public static let longitude = Column(CodingKeys.longitude)
	}
}

extension Location.Summary: FetchableRecord {}

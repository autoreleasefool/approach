import CloudKit
import GRDB
import Harmony
import ModelsLibrary

extension BowlerPreferredGear {
	public struct Database: Sendable, Codable, Equatable, Identifiable {
		public let bowlerId: Bowler.ID
		public let gearId: Gear.ID
		public var archivedRecordData: Data?

		public var id: String { "\(bowlerId)-\(gearId)" }

		public init(bowlerId: Bowler.ID, gearId: Gear.ID) {
			self.bowlerId = bowlerId
			self.gearId = gearId
		}
	}
}

extension BowlerPreferredGear.Database: HRecord {
	public var zoneID: CKRecordZone.ID {
		.init(zoneName: Self.databaseTableName, ownerName: CKCurrentUserDefaultName)
	}

	public var record: CKRecord {
		let encoder = CKRecordEncoder(zoneID: zoneID)
		return try! encoder.encode(self)
	}
}

extension BowlerPreferredGear.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "bowlerPreferredGear"
}

extension BowlerPreferredGear.Database {
	public enum Columns {
		public static let bowlerId = Column(CodingKeys.bowlerId)
		public static let gearId = Column(CodingKeys.gearId)
	}
}

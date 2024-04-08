import CloudKit
import GRDB
import Harmony
import ModelsLibrary

extension GameGear {
	public struct Database: Sendable, Codable, Equatable {
		public let gameId: Game.ID
		public let gearId: Gear.ID
		public var archivedRecordData: Data?

		public init(gameId: Game.ID, gearId: Gear.ID) {
			self.gameId = gameId
			self.gearId = gearId
		}
	}
}

extension GameGear.Database: HRecord {
	public var zoneID: CKRecordZone.ID {
		.init(zoneName: Self.databaseTableName, ownerName: CKCurrentUserDefaultName)
	}

	public var record: CKRecord {
		let encoder = CKRecordEncoder(zoneID: zoneID)
		return try! encoder.encode(self)
	}
}

extension GameGear.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "gameGear"
}

extension GameGear.Database {
	public enum Columns {
		public static let gameId = Column(CodingKeys.gameId)
		public static let gearId = Column(CodingKeys.gearId)
	}
}

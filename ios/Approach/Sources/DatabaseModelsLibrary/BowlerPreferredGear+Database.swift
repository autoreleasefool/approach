import GRDB
import ModelsLibrary

extension BowlerPreferredGear {
	public struct Database: Sendable, Codable, Equatable {
		public let bowlerId: Bowler.ID
		public let gearId: Gear.ID

		public init(bowlerId: Bowler.ID, gearId: Gear.ID) {
			self.bowlerId = bowlerId
			self.gearId = gearId
		}
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

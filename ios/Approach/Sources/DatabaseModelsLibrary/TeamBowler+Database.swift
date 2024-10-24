import Foundation
import GRDB
import ModelsLibrary

extension TeamBowler {
	public struct Database: Sendable, Codable, Equatable {
		public let teamId: Team.ID
		public let bowlerId: Bowler.ID
		public var position: Int

		public init(teamId: Team.ID, bowlerId: Bowler.ID, position: Int) {
			self.teamId = teamId
			self.bowlerId = bowlerId
			self.position = position
		}
	}
}

extension TeamBowler.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName: String = "teamBowler"
}

extension TeamBowler.Database {
	public enum Columns {
		public static let teamId = Column(CodingKeys.teamId)
		public static let bowlerId = Column(CodingKeys.bowlerId)
		public static let position = Column(CodingKeys.position)
	}
}

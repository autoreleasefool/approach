import Foundation
import GRDB
import ModelsLibrary

extension TeamSeries {
	public struct Database: Archivable, Sendable, Identifiable, Codable, Equatable {
		public let id: TeamSeries.ID
		public let teamId: Team.ID
		public var date: Date
		public var archivedOn: Date?

		public init(
			id: TeamSeries.ID,
			teamId: Team.ID,
			date: Date,
			archivedOn: Date?
		) {
			self.id = id
			self.teamId = teamId
			self.date = date
			self.archivedOn = archivedOn
		}
	}
}

extension TeamSeries.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName: String = "teamSeries"
}

extension TeamSeries.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let teamId = Column(CodingKeys.teamId)
		public static let date = Column(CodingKeys.date)
		public static let archivedOn = Column(CodingKeys.archivedOn)
	}
}

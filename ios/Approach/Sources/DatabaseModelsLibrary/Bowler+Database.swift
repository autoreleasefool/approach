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
	public static let leaguesForStatistics =
		hasMany(League.Database.self)
			.filter(League.Database.Columns.excludeFromStatistics == League.ExcludeFromStatistics.include)
	public static let seriesForStatistics =
		hasMany(Series.Database.self, through: leaguesForStatistics, using: League.Database.series)
			.filter(Series.Database.Columns.excludeFromStatistics == Series.ExcludeFromStatistics.include)
	public static let gamesForStatistics =
		hasMany(Game.Database.self, through: seriesForStatistics, using: Series.Database.games)
			.filter(Game.Database.Columns.excludeFromStatistics == Game.ExcludeFromStatistics.include)
	public static let framesForStatistics =
		hasMany(Frame.Database.self, through: gamesForStatistics, using: Game.Database.frames)
}

extension Bowler.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let status = Column(CodingKeys.status)
	}
}

extension Bowler.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension Bowler.List: TableRecord, FetchableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

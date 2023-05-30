import Foundation
import GRDB
import ModelsLibrary

extension Bowler {
	public struct Database: Sendable, Identifiable, Codable, Equatable, TableRecord {
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
	public static let trackableLeagues = hasMany(League.Database.self)
		.filter(League.Database.Columns.excludeFromStatistics == League.ExcludeFromStatistics.include)
	public static let trackableSeries = hasMany(
		Series.Database.self,
		through: trackableLeagues,
		using: League.Database.trackableSeries
	)
		.order(Series.Database.Columns.date.asc)
	public static let trackableGames = hasMany(
		Game.Database.self,
		through: trackableSeries,
		using: Series.Database.trackableGames
	)
	public static let trackableFrames = hasMany(
		Frame.Database.self,
		through: trackableGames,
		using: Game.Database.frames
	)
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

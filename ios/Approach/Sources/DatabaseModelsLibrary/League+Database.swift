import Foundation
import GRDB
import ModelsLibrary

extension League {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "league"

		public let bowlerId: Bowler.ID
		public let id: League.ID
		public var name: String
		public var recurrence: Recurrence
		public var numberOfGames: Int?
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var alleyId: Alley.ID?

		public init(
			bowlerId: Bowler.ID,
			id: League.ID,
			name: String,
			recurrence: Recurrence,
			numberOfGames: Int?,
			additionalPinfall: Int?,
			additionalGames: Int?,
			excludeFromStatistics: ExcludeFromStatistics,
			alleyId: Alley.ID?
		) {
			self.bowlerId = bowlerId
			self.id = id
			self.name = name
			self.recurrence = recurrence
			self.numberOfGames = numberOfGames
			self.additionalPinfall = additionalPinfall
			self.additionalGames = additionalGames
			self.excludeFromStatistics = excludeFromStatistics
			self.alleyId = alleyId
		}
	}
}

extension League.Recurrence: DatabaseValueConvertible {}
extension League.ExcludeFromStatistics: DatabaseValueConvertible {}

extension League.Database: FetchableRecord, PersistableRecord {
	public static let bowler = belongsTo(Bowler.Database.self)
	public static let alley = belongsTo(Alley.Database.self)
	public static let series = hasMany(Series.Database.self)

	public static let trackableSeries = hasMany(Series.Database.self)
		.filter(Series.Database.Columns.excludeFromStatistics == Series.ExcludeFromStatistics.include)
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

extension League.Database {
	public enum Columns {
		public static let bowlerId = Column(CodingKeys.bowlerId)
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let recurrence = Column(CodingKeys.recurrence)
		public static let numberOfGames = Column(CodingKeys.numberOfGames)
		public static let additionalPinfall = Column(CodingKeys.additionalPinfall)
		public static let additionalGames = Column(CodingKeys.additionalGames)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
		public static let alleyId = Column(CodingKeys.alleyId)
	}
}

extension League.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = League.Database.databaseTableName
}

extension League.List: TableRecord, FetchableRecord {
	public static let databaseTableName = League.Database.databaseTableName
}

extension League.Database {
	public struct Inserted {
		public let id: League.ID
		public let recurrence: League.Recurrence
		public let numberOfGames: Int?
		public let excludeFromStatistics: League.ExcludeFromStatistics
		public let alleyId: Alley.ID?

		public init(
			id: League.ID,
			recurrence: League.Recurrence,
			numberOfGames: Int?,
			excludeFromStatistics: League.ExcludeFromStatistics,
			alleyId: Alley.ID?
		) {
			self.id = id
			self.recurrence = recurrence
			self.numberOfGames = numberOfGames
			self.excludeFromStatistics = excludeFromStatistics
			self.alleyId = alleyId
		}
	}
}

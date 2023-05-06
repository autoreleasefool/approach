import Foundation
import GRDB
import ModelsLibrary

extension Game {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "game"

		public let seriesId: Series.ID
		public let id: Game.ID
		public var index: Int
		public var locked: Lock
		public var manualScore: Int?
		public var excludeFromStatistics: ExcludeFromStatistics

		public init(
			seriesId: Series.ID,
			id: Game.ID,
			index: Int,
			locked: Lock,
			manualScore: Int?,
			excludeFromStatistics: ExcludeFromStatistics
		) {
			self.seriesId = seriesId
			self.id = id
			self.index = index
			self.locked = locked
			self.manualScore = manualScore
			self.excludeFromStatistics = excludeFromStatistics
		}
	}
}

extension Game.Lock: DatabaseValueConvertible {}
extension Game.ExcludeFromStatistics: DatabaseValueConvertible {}

extension Game.Database: FetchableRecord, PersistableRecord {
	public static let series = belongsTo(Series.Database.self)
	public static let league = hasOne(League.Database.self, through: series, using: Series.Database.league)
	public static let bowler = hasOne(Bowler.Database.self, through: league, using: League.Database.bowler)
}

extension Game.Database {
	public enum Columns {
		public static let seriesId = Column(CodingKeys.seriesId)
		public static let id = Column(CodingKeys.id)
		public static let index = Column(CodingKeys.index)
		public static let locked = Column(CodingKeys.locked)
		public static let manualScore = Column(CodingKeys.manualScore)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
	}
}

extension Game.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

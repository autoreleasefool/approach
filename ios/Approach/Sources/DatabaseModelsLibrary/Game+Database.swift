import Foundation
import GRDB
import ModelsLibrary

extension Game {
	public struct Database: Sendable, Identifiable, Codable, Equatable, TableRecord {
		public static let databaseTableName = "game"

		public let seriesId: Series.ID
		public let id: Game.ID
		public var index: Int
		public var score: Int
		public var locked: Lock
		public var scoringMethod: ScoringMethod
		public var excludeFromStatistics: ExcludeFromStatistics

		public init(
			seriesId: Series.ID,
			id: Game.ID,
			index: Int,
			score: Int,
			locked: Lock,
			scoringMethod: ScoringMethod,
			excludeFromStatistics: ExcludeFromStatistics
		) {
			self.seriesId = seriesId
			self.id = id
			self.index = index
			self.score = score
			self.locked = locked
			self.scoringMethod = scoringMethod
			self.excludeFromStatistics = excludeFromStatistics
		}
	}
}

extension Game.Lock: DatabaseValueConvertible {}
extension Game.ExcludeFromStatistics: DatabaseValueConvertible {}
extension Game.ScoringMethod: DatabaseValueConvertible {}

extension Game.Database: FetchableRecord, PersistableRecord {
	public static let series = belongsTo(Series.Database.self)
	public static let league = hasOne(League.Database.self, through: series, using: Series.Database.league)
	public static let bowler = hasOne(Bowler.Database.self, through: league, using: League.Database.bowler)
	public static let frames = hasMany(Frame.Database.self)
	public static let matchPlay = hasOne(MatchPlay.Database.self)
}

extension Game.Database {
	public enum Columns {
		public static let seriesId = Column(CodingKeys.seriesId)
		public static let id = Column(CodingKeys.id)
		public static let index = Column(CodingKeys.index)
		public static let score = Column(CodingKeys.score)
		public static let locked = Column(CodingKeys.locked)
		public static let scoringMethod = Column(CodingKeys.scoringMethod)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
	}
}

extension Game.List: TableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

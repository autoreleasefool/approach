import Foundation
import GRDB
import ModelsLibrary

extension Game {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
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

extension Game.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "game"
}

extension Game.Lock: DatabaseValueConvertible {}
extension Game.ExcludeFromStatistics: DatabaseValueConvertible {}
extension Game.ScoringMethod: DatabaseValueConvertible {}

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

extension DerivableRequest<Game.Database> {
	public func orderByIndex() -> Self {
		return order(Game.Database.Columns.index)
	}

	public func filter(bySeries: Series.ID) -> Self {
		return filter(Game.Database.Columns.seriesId == bySeries)
	}
}

extension Game.List: FetchableRecord {}

extension Game.ListMatch: FetchableRecord {}

extension Game.Summary: FetchableRecord {}

extension Game.Shareable: FetchableRecord {}

import Foundation
import GRDB
import ModelsLibrary

extension Game {
	public struct Database: Archivable, Sendable, Identifiable, Codable, Equatable {
		public let seriesId: Series.ID
		public let id: Game.ID
		public var index: Int
		public var score: Int
		public var locked: Lock
		public var scoringMethod: ScoringMethod
		public var excludeFromStatistics: ExcludeFromStatistics
		public var duration: TimeInterval
		public var archivedOn: Date?

		public init(
			seriesId: Series.ID,
			id: Game.ID,
			index: Int,
			score: Int,
			locked: Lock,
			scoringMethod: ScoringMethod,
			excludeFromStatistics: ExcludeFromStatistics,
			duration: TimeInterval,
			archivedOn: Date?
		) {
			self.seriesId = seriesId
			self.id = id
			self.index = index
			self.score = score
			self.locked = locked
			self.scoringMethod = scoringMethod
			self.excludeFromStatistics = excludeFromStatistics
			self.duration = duration
			self.archivedOn = archivedOn
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
		public static let duration = Column(CodingKeys.duration)
		public static let archivedOn = Column(CodingKeys.archivedOn)
	}
}

extension DerivableRequest<Game.Database> {
	public func trackable(includingExcluded: Bool) -> Self {
		let request = self
			.filter(Game.Database.Columns.score > 0)
			.isNotArchived()
		return includingExcluded
			? request
			: request.filter(Game.Database.Columns.excludeFromStatistics == Game.ExcludeFromStatistics.include)
	}
}

extension Game.List: FetchableRecord {}

extension Game.ListMatch: FetchableRecord {}

extension Game.Summary: FetchableRecord {}

extension Game.Shareable: FetchableRecord {}

extension Game.Archived: FetchableRecord {}

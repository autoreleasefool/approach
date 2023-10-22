import Foundation
import GRDB
import ModelsLibrary

extension Series {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let leagueId: League.ID
		public let id: Series.ID
		public var date: Date
		public var numberOfGames: Int
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var alleyId: Alley.ID?

		public init(
			leagueId: League.ID,
			id: Series.ID,
			date: Date,
			numberOfGames: Int,
			preBowl: PreBowl,
			excludeFromStatistics: ExcludeFromStatistics,
			alleyId: Alley.ID?
		) {
			self.leagueId = leagueId
			self.id = id
			self.date = date
			self.numberOfGames = numberOfGames
			self.preBowl = preBowl
			self.excludeFromStatistics = excludeFromStatistics
			self.alleyId = alleyId
		}
	}
}

extension Series.PreBowl: DatabaseValueConvertible {}
extension Series.ExcludeFromStatistics: DatabaseValueConvertible {}

extension Series.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "series"
}

extension Series.Database {
	public enum Columns {
		public static let leagueId = Column(CodingKeys.leagueId)
		public static let id = Column(CodingKeys.id)
		public static let date = Column(CodingKeys.date)
		public static let numberOfGames = Column(CodingKeys.numberOfGames)
		public static let preBowl = Column(CodingKeys.preBowl)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
		public static let alleyId = Column(CodingKeys.alleyId)
	}
}

extension DerivableRequest<Series.Database> {
	public func orderByDate() -> Self {
		let date = Series.Database.Columns.date
		return order(date.desc)
	}

	public func bowled(inLeague: League.ID) -> Self {
		let league = Series.Database.Columns.leagueId
		return filter(league == inLeague)
	}
}


extension Series.Summary: FetchableRecord {}
extension Series.List: FetchableRecord {}

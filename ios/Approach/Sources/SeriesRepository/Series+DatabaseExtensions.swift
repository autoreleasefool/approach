import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary
import SeriesRepositoryInterface

// MARK: - Edit

extension Series.Edit: FetchableRecord, PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	public typealias Columns = Series.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
		container[Columns.date] = date
		container[Columns.appliedDate] = appliedDate
		container[Columns.preBowl] = preBowl
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}
}

extension Series.EditLeague: PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	public typealias Columns = Series.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
	}
}

// MARK: - Create

extension Series.Create: PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	public typealias Columns = Series.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
		container[Columns.date] = date
		container[Columns.appliedDate] = appliedDate
		container[Columns.preBowl] = preBowl
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}
}

// MARK: HighestIndex

extension Series {
	struct HighestIndex: Decodable, FetchableRecord {
		public let leagueId: League.ID
		public let appliedDate: Date?
		public let excludeFromStatistics: ExcludeFromStatistics
		public let maxGameIndex: Int
	}
}

import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import SeriesRepositoryInterface

// MARK: - Edit

extension Series.Edit: FetchableRecord, PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	typealias Columns = Series.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
		container[Columns.numberOfGames] = numberOfGames
		container[Columns.date] = date
		container[Columns.preBowl] = preBowl
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}
}

// MARK: - Create

extension Series.Create: PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	typealias Columns = Series.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
		container[Columns.numberOfGames] = numberOfGames
		container[Columns.date] = date
		container[Columns.preBowl] = preBowl
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}
}

import DatabaseModelsLibrary
import GRDB
import LeaguesRepositoryInterface
import ModelsLibrary

// MARK: - Edit

extension League.Edit: FetchableRecord, PersistableRecord {
	public static let databaseTableName = League.Database.databaseTableName
	typealias Columns = League.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.id] = id
		container[Columns.name] = name
		container[Columns.additionalPinfall] = additionalPinfall
		container[Columns.additionalGames] = additionalGames
		container[Columns.excludeFromStatistics] = excludeFromStatistics
	}
}

// MARK: - Create

extension League.Create: PersistableRecord {
	public static let databaseTableName = League.Database.databaseTableName
	typealias Columns = League.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.bowlerId] = bowlerId
		container[Columns.id] = id
		container[Columns.name] = name
		container[Columns.recurrence] = recurrence
		container[Columns.defaultNumberOfGames] = defaultNumberOfGames
		container[Columns.additionalPinfall] = additionalPinfall
		container[Columns.additionalGames] = additionalGames
		container[Columns.excludeFromStatistics] = excludeFromStatistics
	}
}

// MARK: - SeriesHost

extension League.SeriesHost: FetchableRecord {}

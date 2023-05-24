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
		container[Columns.alleyId] = location?.id
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
		container[Columns.numberOfGames] = numberOfGames
		container[Columns.additionalPinfall] = additionalPinfall
		container[Columns.additionalGames] = additionalGames
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}
}

// MARK: - Base

extension League.SeriesHost: TableRecord, FetchableRecord {
	public static let databaseTableName = League.Database.databaseTableName
}

extension DerivableRequest<League.Database> {
	func orderByName() -> Self {
		let name = League.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func bowled(byBowler: Bowler.ID) -> Self {
		let bowler = League.Database.Columns.bowlerId
		return filter(bowler == byBowler)
	}

	func filter(byRecurrence: League.Recurrence?) -> Self {
		guard let byRecurrence else { return self }
		let recurrence = League.Database.Columns.recurrence
		return filter(recurrence == byRecurrence)
	}
}

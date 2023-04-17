import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import SeriesRepositoryInterface

extension Series.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
}

extension Alley.Summary: TableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}

extension Series.Edit: FetchableRecord, PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	typealias Columns = Series.Database.Columns

	static let seriesLocation = hasOne(Alley.Summary.self)
	var seriesLocation: QueryInterfaceRequest<Alley.Summary> { request(for: Series.Edit.seriesLocation) }

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
		container[Columns.numberOfGames] = numberOfGames
		container[Columns.date] = date
		container[Columns.preBowl] = preBowl
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}

	static func withLocation() -> QueryInterfaceRequest<Series.Edit> {
		return including(optional: Series.Edit.seriesLocation.forKey("location"))
	}
}

extension Series.Create: PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
	typealias Columns = Series.Database.Columns

	static let seriesLocation = hasOne(Alley.Summary.self)
	var seriesLocation: QueryInterfaceRequest<Alley.Summary> { request(for: Series.Create.seriesLocation) }

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.leagueId] = leagueId
		container[Columns.id] = id
		container[Columns.numberOfGames] = numberOfGames
		container[Columns.date] = date
		container[Columns.preBowl] = preBowl
		container[Columns.excludeFromStatistics] = excludeFromStatistics
		container[Columns.alleyId] = location?.id
	}

	static func withLocation() -> QueryInterfaceRequest<Series.Create> {
		return including(optional: Series.Create.seriesLocation.forKey("location"))
	}
}

extension DerivableRequest<Series.Summary> {
	func orderByDate() -> Self {
		let date = Series.Database.Columns.date
		return order(date.desc)
	}

	func bowled(inLeague: League.ID) -> Self {
		let league = Series.Database.Columns.leagueId
		return filter(league == inLeague)
	}
}

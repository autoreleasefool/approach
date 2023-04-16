import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import SeriesRepositoryInterface

extension Series.Editable: FetchableRecord, PersistableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
}

extension Series.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
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

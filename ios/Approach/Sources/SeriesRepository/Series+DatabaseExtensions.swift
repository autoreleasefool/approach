import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import SeriesRepositoryInterface

extension Series.Editable: FetchableRecord, PersistableRecord {
	public static let databaseTableName = Series.DatabaseModel.databaseTableName
}

extension Series.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Series.DatabaseModel.databaseTableName
}

extension DerivableRequest<Series.Summary> {
	func orderByDate() -> Self {
		let date = Series.DatabaseModel.Columns.date
		return order(date.desc)
	}

	func bowled(inLeague: League.ID) -> Self {
		let league = Series.DatabaseModel.Columns.league
		return filter(league == inLeague)
	}
}

import BowlersRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Bowler.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension Bowler.Create: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension Bowler.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension DerivableRequest<Bowler.Summary> {
	func orderByName() -> Self {
		let name = Bowler.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byStatus: Bowler.Status) -> Self {
		let status = Bowler.Database.Columns.status
		return filter(status == byStatus)
	}
}

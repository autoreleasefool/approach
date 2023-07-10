import BowlersRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Bowler.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension Bowler.Create: PersistableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension DerivableRequest<Bowler.Database> {
	func orderByName() -> Self {
		let name = Bowler.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byKind: Bowler.Kind?) -> Self {
		guard let byKind else { return self }
		let kind = Bowler.Database.Columns.kind
		return filter(kind == byKind)
	}
}

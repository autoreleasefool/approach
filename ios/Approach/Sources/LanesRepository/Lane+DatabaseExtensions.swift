import DatabaseModelsLibrary
import GRDB
import LanesRepositoryInterface
import ModelsLibrary

extension Lane.Create: PersistableRecord {
	public static let databaseTableName = Lane.Database.databaseTableName
}

extension Lane.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Lane.Database.databaseTableName
}

extension DerivableRequest<Lane.Edit> {
	func orderByLabel() -> Self {
		let label = Lane.Database.Columns.label
		return order(label.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byAlley: Alley.ID) -> Self {
		let alley = Lane.Database.Columns.alley
		return filter(alley == byAlley)
	}
}

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

extension DerivableRequest<Lane.Database> {
	func orderByLabel() -> Self {
		let label = Lane.Database.Columns.label
		return order(label.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byAlley: Alley.ID?) -> Self {
		guard let byAlley else { return self }
		let alley = Lane.Database.Columns.alleyId
		return filter(alley == byAlley)
	}
}

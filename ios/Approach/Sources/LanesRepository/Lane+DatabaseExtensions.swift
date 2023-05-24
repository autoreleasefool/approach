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

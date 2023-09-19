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

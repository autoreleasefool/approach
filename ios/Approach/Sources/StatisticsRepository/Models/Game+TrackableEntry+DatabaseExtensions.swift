import GRDB
import DatabaseModelsLibrary
import ModelsLibrary
import StatisticsLibrary

extension Game.TrackableEntry: FetchableRecord, TableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Game.TrackableEntry: FetchableRecord, TableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

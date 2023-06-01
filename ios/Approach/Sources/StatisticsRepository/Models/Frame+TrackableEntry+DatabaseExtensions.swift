import GRDB
import DatabaseModelsLibrary
import ModelsLibrary
import StatisticsLibrary

extension Frame.TrackableEntry: FetchableRecord, TableRecord {
	public static let databaseTableName = Frame.Database.databaseTableName
}
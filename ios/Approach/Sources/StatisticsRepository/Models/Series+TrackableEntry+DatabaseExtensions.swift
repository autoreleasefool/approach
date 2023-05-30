import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Series.TrackableEntry: FetchableRecord, TableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
}

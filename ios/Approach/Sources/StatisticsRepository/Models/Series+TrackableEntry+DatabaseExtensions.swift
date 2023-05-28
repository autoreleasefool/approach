import GRDB
import DatabaseModelsLibrary
import ModelsLibrary
import StatisticsLibrary

extension Series.TrackableEntry: FetchableRecord, TableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
}

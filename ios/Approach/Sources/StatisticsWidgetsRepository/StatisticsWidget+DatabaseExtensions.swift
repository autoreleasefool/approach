import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsWidgetsRepositoryInterface

extension StatisticsWidget.Create: PersistableRecord, FetchableRecord {
	public static let databaseTableName = StatisticsWidget.Database.databaseTableName
}

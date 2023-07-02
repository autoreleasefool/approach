import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsWidgetsRepositoryInterface

extension StatisticsWidget.Create: PersistableRecord, FetchableRecord {
	public static let databaseTableName = StatisticsWidget.Database.databaseTableName
}

extension DerivableRequest<StatisticsWidget.Database> {
	func orderByPriority() -> Self {
		let priority = StatisticsWidget.Database.Columns.priority
		return order(priority.asc)
	}

	func filter(byContext: String?) -> Self {
		guard let byContext else { return self }
		let context = StatisticsWidget.Database.Columns.context
		return filter(context == byContext)
	}
}

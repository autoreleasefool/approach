import AlleysRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Alley.Editable: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Alley.DatabaseModel.databaseTableName
}

extension Alley.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Alley.DatabaseModel.databaseTableName
}

extension DerivableRequest<Alley.Summary> {
	func orderByName() -> Self {
		let name = Alley.DatabaseModel.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(by filter: Alley.FetchRequest.Filter) -> Self {
		var query = self
		if let material = filter.material {
			query = query.filter(Alley.DatabaseModel.Columns.material == material)
		}
		if let pinFall = filter.pinFall {
			query = query.filter(Alley.DatabaseModel.Columns.pinFall == pinFall)
		}
		if let mechanism = filter.mechanism {
			query = query.filter(Alley.DatabaseModel.Columns.mechanism == mechanism)
		}
		if let pinBase = filter.pinBase {
			query = query.filter(Alley.DatabaseModel.Columns.pinBase == pinBase)
		}
		return query
	}
}

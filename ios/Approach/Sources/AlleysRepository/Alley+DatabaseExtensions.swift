import AlleysRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Alley.Editable: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}

extension Alley.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}

extension DerivableRequest<Alley.Summary> {
	func orderByName() -> Self {
		let name = Alley.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(by filter: Alley.FetchRequest.Filter) -> Self {
		var query = self
		if let material = filter.material {
			query = query.filter(Alley.Database.Columns.material == material)
		}
		if let pinFall = filter.pinFall {
			query = query.filter(Alley.Database.Columns.pinFall == pinFall)
		}
		if let mechanism = filter.mechanism {
			query = query.filter(Alley.Database.Columns.mechanism == mechanism)
		}
		if let pinBase = filter.pinBase {
			query = query.filter(Alley.Database.Columns.pinBase == pinBase)
		}
		return query
	}
}

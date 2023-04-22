import AlleysRepositoryInterface
import LanesRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Alley.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}

extension Alley.EditWithLanes: TableRecord, FetchableRecord, EncodableRecord {}

extension Lane.Edit: FetchableRecord {
	public static let databaseTableName = Lane.Database.databaseTableName
}

extension DerivableRequest<Alley.Database> {
	func orderByName() -> Self {
		let name = Alley.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(
		_ material: Alley.Material?,
		_ pinFall: Alley.PinFall?,
		_ mechanism: Alley.Mechanism?,
		_ pinBase: Alley.PinBase?
	) -> Self {
		var query = self
		if let material {
			query = query.filter(Alley.Database.Columns.material == material)
		}
		if let pinFall {
			query = query.filter(Alley.Database.Columns.pinFall == pinFall)
		}
		if let mechanism {
			query = query.filter(Alley.Database.Columns.mechanism == mechanism)
		}
		if let pinBase {
			query = query.filter(Alley.Database.Columns.pinBase == pinBase)
		}
		return query
	}
}

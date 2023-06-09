import AlleysRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import LanesRepositoryInterface
import ModelsLibrary

extension Alley.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
	typealias Columns = Alley.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.id] = id
		container[Columns.name] = name
		container[Columns.material] = material
		container[Columns.mechanism] = mechanism
		container[Columns.pinBase] = pinBase
		container[Columns.pinFall] = pinFall
		container[Columns.locationId] = location?.id
	}
}

extension Alley.EditWithLanes: FetchableRecord {}

extension Lane.Edit: FetchableRecord {}

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

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

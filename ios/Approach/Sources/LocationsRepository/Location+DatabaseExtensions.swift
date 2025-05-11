import DatabaseModelsLibrary
import GRDB
import LocationsRepositoryInterface
import ModelsLibrary

extension Location.Summary: PersistableRecord {}

extension Location.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Location.Database.databaseTableName
	public typealias Columns = Location.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.id] = id
		container[Columns.title] = title
		container[Columns.subtitle] = subtitle
		container[Columns.latitude] = coordinate.latitude
		container[Columns.longitude] = coordinate.longitude
	}
}

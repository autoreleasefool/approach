import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension League.FetchRequest: Fetchable {
	func fetchValues(_ db: Database) throws -> [League] {
		switch ordering {
		case .byLastModified:
			return try League.all()
				.order(Column("lastModifiedAt").desc)
				.fetchAll(db)
		}
	}
}

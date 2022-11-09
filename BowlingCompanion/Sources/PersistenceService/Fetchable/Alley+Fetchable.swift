import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Alley.FetchRequest: Fetchable {
	func fetchValues(_ db: Database) throws -> [Alley] {
		var query = Alley.all()

		switch ordering {
		case .byLastModified:
			query = query.order(Column("lastModifiedAt").desc)
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

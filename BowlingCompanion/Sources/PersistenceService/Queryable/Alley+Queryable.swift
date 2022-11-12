import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Alley.Query: Queryable {
	func fetchValues(_ db: Database) throws -> [Alley] {
		var query = Alley.all()

		switch ordering {
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

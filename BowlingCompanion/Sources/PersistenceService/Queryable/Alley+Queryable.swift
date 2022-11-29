import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Alley.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Alley] {
		var query = Alley.all()

		switch filter {
		case let .id(id):
			query = query.filter(Column("id") == id)
		case .none:
			break
		}

		switch ordering {
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

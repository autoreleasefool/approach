import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension League.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [League] {
		var query = League.all()
			.filter(Column("bowler") == bowler)

		switch ordering {
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

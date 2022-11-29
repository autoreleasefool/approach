import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Bowler.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Bowler] {
		var query = Bowler.all()

		switch ordering {
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

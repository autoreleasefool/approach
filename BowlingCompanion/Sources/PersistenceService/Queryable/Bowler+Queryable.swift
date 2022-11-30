import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Bowler.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Bowler] {
		var query = Bowler.all()

		filter.forEach {
			switch $0 {
			case let .id(id):
				query = query.filter(Column("id") == id)
			case let .name(name):
				query = query.filter(Column("name") == name)
			}
		}

		switch ordering {
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

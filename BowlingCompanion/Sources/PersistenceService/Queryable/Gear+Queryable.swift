import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Gear.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Gear] {
		var query = Gear.all()

		if let bowler {
			query = query.filter(Column("bowler") == bowler)
		}

		if let kind {
			query = query.filter(Column("kind") == kind.rawValue)
		}

		switch ordering {
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

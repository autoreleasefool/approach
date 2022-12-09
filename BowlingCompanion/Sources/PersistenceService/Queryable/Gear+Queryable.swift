import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Gear.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Gear] {
		var query = Gear.all()

		if let bowler {
			query = query.filter(Column("bowler") == bowler)
		}

		if let kind {
			query = query.filter(Column("kind") == kind.rawValue)
		}

		switch ordering {
		case .byName, .byRecentlyUsed:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

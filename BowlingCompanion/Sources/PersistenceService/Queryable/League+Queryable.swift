import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension League.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [League] {
		var query = League.all()

		filter.forEach {
			switch $0 {
			case let .id(id):
				query = query.filter(id: id)
			case let .bowler(bowler):
				query = query.filter(Column("bowler") == bowler)
			case let .recurrence(recurrence):
				query = query.filter(Column("recurrence") == recurrence.rawValue)
			}
		}

		switch ordering {
		case .byName, .byRecentlyUsed:
			query = query.order(Column("name").collating(.localizedCaseInsensitiveCompare))
		}

		return try query.fetchAll(db)
	}
}

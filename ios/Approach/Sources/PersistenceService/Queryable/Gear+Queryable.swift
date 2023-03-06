import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Gear.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Gear] {
		var query = Gear.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .bowler(bowler):
			query = bowler.gear
		case let .kind(kind):
			query = query.filter(Column("kind") == kind.rawValue)
		case .none:
			break
		}

		switch ordering {
		case .byName, .byRecentlyUsed:
			query = query.order(Column("name").collating(.localizedCaseInsensitiveCompare))
		}

		return try query.fetchAll(db)
	}
}

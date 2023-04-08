import GRDB
import PersistenceServiceInterface
import SharedModelsFetchableLibrary
import SharedModelsLibrary
import SharedModelsPersistableLibrary

extension Bowler.SingleFetchRequest: SingleQueryable {
	@Sendable func fetchValue(_ db: Database) throws -> Bowler? {
		switch filter {
		case let .id(id):
			return try Bowler.fetchOne(db, id: id)
		case let .owner(game):
			return try game.bowler.fetchOne(db)
		}
	}
}

extension Bowler.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Bowler] {
		var query = Bowler.all()

		switch filter {
		case let .name(name):
			query = query.filter(Column("name").like(name))
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

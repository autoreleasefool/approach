import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Bowler.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Bowler] {
		var query = Bowler.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .name(name):
			query = query.filter(Column("name").like(name))
		case let .team(team):
			query = team.bowlers
		case let .forGame(game):
			query = game.bowler
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

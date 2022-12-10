import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Frame.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Frame] {
		var query = Frame.all()

		filter.forEach {
			switch $0 {
			case let .id(id):
				query = query.filter(id: id)
			case let .game(game):
				query = query.filter(Column("game") == game)
			}
		}

		switch ordering {
		case .byOrdinal:
			query = query.order(Column("ordinal").asc)
		}

		return try query.fetchAll(db)
	}
}

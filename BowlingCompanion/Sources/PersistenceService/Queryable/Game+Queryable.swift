import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Game.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Game] {
		var query = Game.all()

		filter.forEach {
			switch $0 {
			case let .id(id):
				query = query.filter(id: id)
			case let .series(series):
				query = query.filter(Column("series") == series)
			}
		}

		switch ordering {
		case .byOrdinal:
			query = query.order(Column("ordinal").asc)
		}

		return try query.fetchAll(db)
	}
}

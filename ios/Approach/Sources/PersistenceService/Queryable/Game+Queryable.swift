import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Game.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Game] {
		var query = Game.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .series(series):
			query = series.games
		case .none:
			break
		}

		switch ordering {
		case .byOrdinal:
			query = query.order(Column("ordinal").collating(.localizedCaseInsensitiveCompare))
		}

		return try query.fetchAll(db)
	}
}

import GRDB
import PersistenceServiceInterface
import SharedModelsFetchableLibrary
import SharedModelsLibrary
import SharedModelsPersistableLibrary

extension Frame.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Frame] {
		var query = Frame.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .game(gameId):
			query = query.filter(Column("game") == gameId)
		case .none:
			break
		}

		switch ordering {
		case .byOrdinal:
			query = query.order(Column("ordinal"))
		}

		return try query.fetchAll(db)
	}
}

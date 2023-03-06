import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Frame.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Frame] {
		var query = Frame.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .game(game):
			query = game.frames
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

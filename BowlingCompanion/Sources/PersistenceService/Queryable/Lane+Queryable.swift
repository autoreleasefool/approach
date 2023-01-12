import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Lane.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Lane] {
		var query = Lane.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .alley(id):
			query = query.filter(Column("alley") == id)
		case let .series(series):
			query = series.lanes
		case .none:
			break
		}

		switch ordering {
		case .byLabel:
			query = query.order(Column("label").collating(.localizedCaseInsensitiveCompare))
		}

		return try query.fetchAll(db)
	}
}

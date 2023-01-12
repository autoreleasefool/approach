import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Series.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Series] {
		var query = Series.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .league(league):
			query = query.filter(Column("league") == league)
		case .none:
			break
		}

		switch ordering {
		case .byDate:
			query = query.order(Column("date").desc)
		}

		return try query.fetchAll(db)
	}
}

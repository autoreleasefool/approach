import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Series.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Series] {
		var query = Series.all()
			.filter(Column("league") == league)

		switch ordering {
		case .byDate:
			query = query.order(Column("date").desc)
		}

		return try query.fetchAll(db)
	}
}

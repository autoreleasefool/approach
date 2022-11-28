import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Series.Query: Queryable {
	func fetchValues(_ db: Database) throws -> [Series] {
		var query = Series.all()
			.filter(Column("league") == league)

		switch ordering {
		case .byDate:
			query = query.order(Column("date").desc)
		}

		return try query.fetchAll(db)
	}
}

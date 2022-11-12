import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Frame.Query: Queryable {
	func fetchValues(_ db: Database) throws -> [Frame] {
		switch ordering {
		case .byOrdinal:
			return try Frame.all()
				.filter(Column("gameId") == game)
				.order(Column("ordinal").asc)
				.fetchAll(db)
		}
	}
}

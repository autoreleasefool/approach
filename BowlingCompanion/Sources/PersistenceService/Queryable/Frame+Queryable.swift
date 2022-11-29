import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Frame.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Frame] {
		try Frame.all()
			.filter(Column("game") == game)
			.order(Column("ordinal").asc)
			.fetchAll(db)
	}
}

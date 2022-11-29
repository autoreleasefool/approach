import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Game.Query: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Game] {
		try Game.all()
			.filter(Column("series") == series)
			.order(Column("ordinal").asc)
			.fetchAll(db)
	}
}

import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Game.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Game] {
		try Game.all()
			.filter(Column("series") == series)
			.order(Column("ordinal").asc)
			.fetchAll(db)
	}
}

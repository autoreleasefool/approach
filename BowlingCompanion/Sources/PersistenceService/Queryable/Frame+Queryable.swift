import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Frame.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Frame] {
		try Frame.all()
			.filter(Column("game") == game)
			.order(Column("ordinal").asc)
			.fetchAll(db)
	}
}

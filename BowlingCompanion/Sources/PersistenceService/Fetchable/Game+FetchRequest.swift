import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Game.FetchRequest: Fetchable {
	func fetchValues(_ db: Database) throws -> [Game] {
		switch ordering {
		case .byOrdinal:
			return try Game.all()
				.filter(Column("seriesId") == series)
				.order(Column("ordinal").asc)
				.fetchAll(db)
		}
	}
}

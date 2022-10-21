import GamesDataProviderInterface
import GRDB
import PersistenceModelsLibrary
import SharedModelsLibrary

extension Game.FetchRequest {
	func fetchValue(_ db: Database) throws -> [Game] {
		switch ordering {
		case .byOrdinal:
			return try Game.all()
				.filter(Column("seriesId") == series)
				.order(Column("ordinal").asc)
				.fetchAll(db)
		}
	}
}

import FramesDataProviderInterface
import GRDB
import PersistenceModelsLibrary
import SharedModelsLibrary

extension Frame.FetchRequest {
	func fetchValue(_ db: Database) throws -> [Frame] {
		switch ordering {
		case .byOrdinal:
			return try Frame.all()
				.filter(Column("gameId") == game)
				.order(Column("ordinal").asc)
				.fetchAll(db)
		}
	}
}

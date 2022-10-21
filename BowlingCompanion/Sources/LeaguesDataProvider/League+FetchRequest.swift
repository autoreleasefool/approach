import GRDB
import LeaguesDataProviderInterface
import PersistenceModelsLibrary
import SharedModelsLibrary

extension League.FetchRequest {
	func fetchValue(_ db: Database) throws -> [League] {
		switch ordering {
		case .byLastModified:
			return try League.all()
				.order(Column("lastModifiedAt").desc)
				.fetchAll(db)
		}
	}
}

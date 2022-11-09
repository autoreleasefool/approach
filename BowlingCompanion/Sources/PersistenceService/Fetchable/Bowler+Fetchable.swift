import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Bowler.FetchRequest: Fetchable {
	func fetchValues(_ db: Database) throws -> [Bowler] {
		var query = Bowler.all()

		switch ordering {
		case .byLastModified:
			query = query.order(Column("lastModifiedAt").desc)
		case .byName:
			query = query.order(Column("name").asc)
		}

		return try query.fetchAll(db)
	}
}

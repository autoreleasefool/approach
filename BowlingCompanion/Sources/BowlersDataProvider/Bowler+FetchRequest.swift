import GRDB
import PersistenceModelsLibrary
import SharedModelsLibrary

extension Bowler.FetchRequest {
	func fetchValue(_ db: Database) throws -> [Bowler] {
		switch ordering {
		case .byLastModified:
			return try Bowler.all()
				.order(Column("lastModifiedAt").desc)
				.fetchAll(db)
		case .byName:
			return try Bowler.all()
				.order(Column("name").asc)
				.fetchAll(db)
		}
	}
}

import GRDB
import PersistenceServiceInterface
import SharedPersistenceModelsLibrary
import SharedModelsLibrary

extension Series.FetchRequest: Fetchable {
	func fetchValues(_ db: Database) throws -> [Series] {
		switch ordering {
		case .byDate:
			return try Series.all()
				.filter(Column("leagueId") == league)
				.order(Column("date").desc)
				.fetchAll(db)
		}
	}
}

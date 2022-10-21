import GRDB
import PersistenceModelsLibrary
import SeriesDataProviderInterface
import SharedModelsLibrary

extension Series.FetchRequest {
	func fetchValue(_ db: Database) throws -> [Series] {
		switch ordering {
		case .byDate:
			return try Series.all()
				.filter(Column("leagueId") == league)
				.order(Column("date").desc)
				.fetchAll(db)
		}
	}
}

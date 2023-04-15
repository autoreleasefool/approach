import GamesRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Game.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

extension Game.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

extension DerivableRequest<Game.Summary> {
	func orderByOrdinal() -> Self {
		let ordinal = Game.Database.Columns.ordinal
		return order(ordinal)
	}

	func filter(bySeries: Series.ID) -> Self {
		let series = Game.Database.Columns.series
		return filter(series == bySeries)
	}
}

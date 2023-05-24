import DatabaseModelsLibrary
import GamesRepositoryInterface
import GRDB
import ModelsLibrary

extension Game.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

extension Game.Edit.SeriesInfo: TableRecord, FetchableRecord {
	public static let databaseTableName = Series.Database.databaseTableName
}

extension Game.Edit.AlleyInfo: TableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}

extension Game.Edit.LaneInfo: TableRecord, FetchableRecord {
	public static let databaseTableName = Lane.Database.databaseTableName
}

extension DerivableRequest<Game.Database> {
	func orderByIndex() -> Self {
		return order(Game.Database.Columns.index)
	}

	func filter(bySeries: Series.ID) -> Self {
		return filter(Game.Database.Columns.seriesId == bySeries)
	}
}

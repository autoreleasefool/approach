import DatabaseModelsLibrary
import GamesRepositoryInterface
import GRDB
import ModelsLibrary

extension Game.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

extension Game.Edit.SeriesInfo: FetchableRecord {}

extension Game.Edit.AlleyInfo: FetchableRecord {}

extension Game.Edit.LaneInfo: FetchableRecord {}

extension DerivableRequest<Game.Database> {
	func orderByIndex() -> Self {
		return order(Game.Database.Columns.index)
	}

	func filter(bySeries: Series.ID) -> Self {
		return filter(Game.Database.Columns.seriesId == bySeries)
	}
}

extension Game.Indexed: FetchableRecord {}

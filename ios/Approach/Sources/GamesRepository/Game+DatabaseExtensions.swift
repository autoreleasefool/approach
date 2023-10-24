import DatabaseModelsLibrary
import GamesRepositoryInterface
import GRDB
import ModelsLibrary

extension Game.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Game.Database.databaseTableName
}

extension Game.Edit.SeriesInfo: FetchableRecord {}

extension Game.Edit.AlleyInfo: FetchableRecord {}

extension Game.Indexed: FetchableRecord {}

// MARK: HighestIndex

extension Series {
	struct HighestIndex: Decodable, FetchableRecord {
		public let maxGameIndex: Int
	}
}

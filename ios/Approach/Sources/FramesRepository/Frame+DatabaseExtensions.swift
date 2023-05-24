import DatabaseModelsLibrary
import FramesRepositoryInterface
import GRDB
import ModelsLibrary

extension Frame.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Frame.Database.databaseTableName
}

extension Frame.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Frame.Database.databaseTableName
}

extension Gear.Rolled: TableRecord, FetchableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName
}

extension DerivableRequest<Frame.Database> {
	func orderByIndex() -> Self {
		order(Frame.Database.Columns.index)
	}

	func filter(byGame: Game.ID) -> Self {
		filter(Frame.Database.Columns.gameId == byGame)
	}
}

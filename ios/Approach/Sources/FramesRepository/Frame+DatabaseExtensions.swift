import DatabaseModelsLibrary
import FramesRepositoryInterface
import GRDB
import ModelsLibrary

extension Frame.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Frame.Database.databaseTableName
}

extension Frame.Summary: FetchableRecord {}

extension Gear.Named: FetchableRecord {}

extension DerivableRequest<Frame.Database> {
	func orderByIndex() -> Self {
		order(Frame.Database.Columns.index)
	}

	func filter(byGame: Game.ID) -> Self {
		filter(Frame.Database.Columns.gameId == byGame)
	}
}

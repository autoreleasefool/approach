import DatabaseModelsLibrary
import FramesRepositoryInterface
import GRDB
import ModelsLibrary

extension Frame.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Frame.Database.databaseTableName
}

extension DerivableRequest<Frame.Database> {
	func orderByOrdinal() -> Self {
		let ordinal = Frame.Database.Columns.ordinal
		return order(ordinal)
	}

	func filter(byGame: Game.ID) -> Self {
		let game = Frame.Database.Columns.gameId
		return filter(game == byGame)
	}
}

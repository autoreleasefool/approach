import DatabaseModelsLibrary
import FramesRepositoryInterface
import GRDB
import ModelsLibrary

extension Frame.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Frame.Database.databaseTableName
}

extension Frame.Rolls: FetchableRecord {}

extension Frame.Summary: FetchableRecord {}

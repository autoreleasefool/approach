import DatabaseModelsLibrary
import GRDB
import MatchPlaysRepositoryInterface
import ModelsLibrary

// MARK: - Edit

extension MatchPlay.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = MatchPlay.Database.databaseTableName
	public typealias Columns = MatchPlay.Database.Columns

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.gameId] = gameId
		container[Columns.id] = id
		container[Columns.opponentId] = opponent?.id
		container[Columns.opponentScore] = opponentScore
		container[Columns.result] = result
	}
}

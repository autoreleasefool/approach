import Foundation
import GRDB
import ModelsLibrary

extension MatchPlay {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "matchPlay"

		public let gameId: Game.ID
		public let id: MatchPlay.ID

		public var opponent: Bowler.ID
		public var opponentScore: Int?
		public var result: MatchPlay.Result?

		public init(
			gameId: Game.ID,
			id: MatchPlay.ID,
			opponent: Bowler.ID,
			opponentScore: Int?,
			result: MatchPlay.Result?
		) {
			self.gameId = gameId
			self.id = id
			self.opponent = opponent
			self.opponentScore = opponentScore
			self.result = result
		}
	}
}

extension MatchPlay.Database: FetchableRecord, PersistableRecord {
	public static let opponent = belongsTo(Bowler.Database.self)
}

extension MatchPlay.Database {
	public enum Columns {
		public static let gameId = Column(CodingKeys.gameId)
		public static let id = Column(CodingKeys.id)
		public static let opponent = Column(CodingKeys.opponent)
		public static let opponentScore = Column(CodingKeys.opponentScore)
		public static let result = Column(CodingKeys.result)
	}
}

extension MatchPlay.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = MatchPlay.Database.databaseTableName
}

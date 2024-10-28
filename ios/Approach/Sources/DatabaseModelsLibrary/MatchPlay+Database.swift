import Foundation
import GRDB
import ModelsLibrary

extension MatchPlay {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let gameId: Game.ID
		public let id: MatchPlay.ID

		public var opponentId: Bowler.ID?
		public var opponentScore: Int?
		public var result: MatchPlay.Result?

		public init(
			gameId: Game.ID,
			id: MatchPlay.ID,
			opponentId: Bowler.ID?,
			opponentScore: Int?,
			result: MatchPlay.Result?
		) {
			self.gameId = gameId
			self.id = id
			self.opponentId = opponentId
			self.opponentScore = opponentScore
			self.result = result
		}
	}
}

extension MatchPlay.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "matchPlay"
}

extension MatchPlay.Result: DatabaseValueConvertible {}

extension MatchPlay.Database {
	public enum Columns {
		public static let gameId = Column(CodingKeys.gameId)
		public static let id = Column(CodingKeys.id)
		public static let opponentId = Column(CodingKeys.opponentId)
		public static let opponentScore = Column(CodingKeys.opponentScore)
		public static let result = Column(CodingKeys.result)
	}
}

extension DerivableRequest<MatchPlay.Database> {
	public func filter(byGame: Game.ID) -> Self {
		filter(MatchPlay.Database.Columns.gameId == byGame)
	}
}

extension MatchPlay.Summary: FetchableRecord {}

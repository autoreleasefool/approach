import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct MatchPlaysImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let matchPlayRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, game_id, opponent_id, opponent_score, result FROM match_plays;"
			)

			while let matchPlayRow = try matchPlayRows.next() {
				let id: MatchPlay.ID = matchPlayRow["id"]
				let gameId: Game.ID = matchPlayRow["game_id"]
				let opponentId: Bowler.ID? = matchPlayRow["opponent_id"]
				let opponentScore: Int? = matchPlayRow["score"]
				let result: String? = matchPlayRow["result"]

				let matchPlay = MatchPlay.Database(
					gameId: gameId,
					id: id,
					opponentId: opponentId,
					opponentScore: opponentScore,
					result: MatchPlay.Result(rawValue: result?.snakeCaseToCamelCase ?? "")
				)

				try matchPlay.insert(exportDb)
			}
		}
	}
}

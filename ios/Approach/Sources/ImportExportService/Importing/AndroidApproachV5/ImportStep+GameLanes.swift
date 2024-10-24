import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct GameLanesImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let gameLaneRows = try Row.fetchCursor(importDb, sql: "SELECT game_id, lane_id FROM game_lanes;")

			while let gameLaneRow = try gameLaneRows.next() {
				let gameId: Game.ID = gameLaneRow["game_id"]
				let laneId: Lane.ID = gameLaneRow["lane_id"]

				let gameLane = GameLane.Database(gameId: gameId, laneId: laneId)

				try gameLane.insert(exportDb)
			}
		}
	}
}

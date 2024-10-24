import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct GameGearImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let gameGearRows = try Row.fetchCursor(importDb, sql: "SELECT game_id, gear_id FROM game_gear;")

			while let gameGearRow = try gameGearRows.next() {
				let gameId: Game.ID = gameGearRow["game_id"]
				let gearId: Gear.ID = gameGearRow["gear_id"]

				let gameGear = GameGear.Database(gameId: gameId, gearId: gearId)

				try gameGear.insert(exportDb)
			}
		}
	}
}

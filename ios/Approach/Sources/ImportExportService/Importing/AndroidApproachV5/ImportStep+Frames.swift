import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct FramesImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let frameRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT game_id, `index`, roll0, roll1, roll2, ball0, ball1, ball2 FROM frames;"
			)

			while let frameRow = try frameRows.next() {
				let gameId: Game.ID = frameRow["game_id"]
				let index: Int = frameRow["index"]
				let roll0: String? = frameRow["roll0"]
				let roll1: String? = frameRow["roll1"]
				let roll2: String? = frameRow["roll2"]
				let ball0: Gear.ID? = frameRow["ball0"]
				let ball1: Gear.ID? = frameRow["ball1"]
				let ball2: Gear.ID? = frameRow["ball2"]

				let frame = Frame.Database(
					gameId: gameId,
					index: index,
					roll0: roll0,
					roll1: roll1,
					roll2: roll2,
					ball0: ball0,
					ball1: ball1,
					ball2: ball2
				)

				try frame.insert(exportDb)
			}
		}
	}
}

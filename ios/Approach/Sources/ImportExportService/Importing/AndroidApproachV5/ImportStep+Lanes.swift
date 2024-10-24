import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct LanesImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let laneRows = try Row.fetchCursor(importDb, sql: "SELECT id, alley_id, label, position FROM lanes;")
			while let laneRow = try laneRows.next() {
				let id: Lane.ID = laneRow["id"]
				let alleyId: Alley.ID? = laneRow["alley_id"]
				let label: String = laneRow["label"]
				let position: String = laneRow["position"]

				guard let alleyId else { continue }

				let lane = Lane.Database(
					alleyId: alleyId,
					id: id,
					label: label,
					position: Lane.Position(rawValue: position.snakeCaseToCamelCase) ?? .noWall
				)

				try lane.insert(exportDb)
			}
		}
	}
}

import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct BowlerPreferredGearImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let bowlerPreferredGearRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT bowler_id, gear_id FROM bowler_preferred_gear;"
			)

			while let bowlerPreferredGearRow = try bowlerPreferredGearRows.next() {
				let bowlerId: Bowler.ID = bowlerPreferredGearRow["bowler_id"]
				let gearId: Gear.ID = bowlerPreferredGearRow["gear_id"]

				let bowlerPreferredGear = BowlerPreferredGear.Database(bowlerId: bowlerId, gearId: gearId)

				try bowlerPreferredGear.insert(exportDb)
			}
		}
	}
}

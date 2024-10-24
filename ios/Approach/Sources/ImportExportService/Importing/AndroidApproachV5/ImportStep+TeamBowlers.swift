import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct TeamBowlersImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let teamBowlerRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT team_id, bowler_id, position FROM team_bowler;"
			)

			while let teamBowlerRow = try teamBowlerRows.next() {
				let teamId: Team.ID = teamBowlerRow["team_id"]
				let bowlerId: Bowler.ID = teamBowlerRow["bowler_id"]
				let position: Int = teamBowlerRow["position"]

				let teamBowler = TeamBowler.Database(teamId: teamId, bowlerId: bowlerId, position: position)

				try teamBowler.insert(exportDb)
			}
		}
	}
}

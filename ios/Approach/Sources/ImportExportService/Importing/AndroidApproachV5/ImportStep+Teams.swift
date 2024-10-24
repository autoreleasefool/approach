import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct TeamsImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let teamRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, name FROM teams;"
			)

			while let teamRow = try teamRows.next() {
				let id: Team.ID = teamRow["id"]
				let name: String = teamRow["name"]

				let team = Team.Database(id: id, name: name)

				try team.insert(exportDb)
			}
		}
	}
}

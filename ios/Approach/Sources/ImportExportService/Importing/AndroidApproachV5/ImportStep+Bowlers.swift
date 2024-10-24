import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct BowlersImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let bowlerRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, name, kind, archived_on FROM bowlers;"
			)

			while let bowlerRow = try bowlerRows.next() {
				let id: Bowler.ID = bowlerRow["id"]
				let name: String = bowlerRow["name"]
				let kind: String = bowlerRow["kind"]
				let archivedOn: Double? = bowlerRow["archived_on"]

				let bowler = Bowler.Database(
					id: id,
					name: name,
					kind: Bowler.Kind.init(rawValue: kind.snakeCaseToCamelCase) ?? .playable,
					archivedOn: archivedOn?.instantToDate
				)

				try bowler.insert(exportDb)
			}
		}
	}
}

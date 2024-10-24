import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct AlleysImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let alleyRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, name, material, pin_fall, mechanism, pin_base, location_id FROM alleys;"
			)

			while let alleyRow = try alleyRows.next() {
				let id: Alley.ID = alleyRow["id"]
				let name: String = alleyRow["name"]
				let material: String? = alleyRow["material"]
				let pinFall: String? = alleyRow["pin_fall"]
				let mechanism: String? = alleyRow["mechanism"]
				let pinBase: String? = alleyRow["pin_base"]
				let locationId: Location.ID? = alleyRow["location_id"]

				let alley = Alley.Database(
					id: id,
					name: name,
					material: Alley.Material(rawValue: material?.snakeCaseToCamelCase ?? ""),
					pinFall: Alley.PinFall(rawValue: pinFall?.snakeCaseToCamelCase.lowercased() ?? ""),
					mechanism: Alley.Mechanism(rawValue: mechanism?.snakeCaseToCamelCase ?? ""),
					pinBase: Alley.PinBase(rawValue: pinBase?.snakeCaseToCamelCase ?? ""),
					locationId: locationId
				)

				try alley.insert(exportDb)
			}
		}
	}
}

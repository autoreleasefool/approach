import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct LocationsImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let locationRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, title, subtitle, latitude, longitude FROM locations;"
			)

			while let locationRow = try locationRows.next() {
				let id: Location.ID = locationRow["id"]
				let title: String = locationRow["title"]
				let subtitle: String = locationRow["subtitle"]
				let latitude: Double = locationRow["latitude"]
				let longitude: Double = locationRow["longitude"]

				let location = Location.Database(
					id: id,
					title: title,
					subtitle: subtitle,
					latitude: latitude,
					longitude: longitude
				)

				try location.insert(exportDb)
			}
		}
	}
}

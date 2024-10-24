import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct TeamSeriesImportStep: SQLiteImportStep {
		private let dateFormatter: DateFormatter = {
			let formatter = DateFormatter()
			formatter.locale = Locale(identifier: "en_US")
			formatter.dateFormat = "yyyy-MM-dd"
			return formatter
		}()

		func performImport(from importDb: Database, to exportDb: Database) throws {
			let teamSeriesRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, team_id, date, archived_on FROM team_series;"
			)

			while let teamSeriesRow = try teamSeriesRows.next() {
				let id: TeamSeries.ID = teamSeriesRow["id"]
				let teamId: Team.ID = teamSeriesRow["team_id"]
				let date: String = teamSeriesRow["date"]
				let archivedOn: Double? = teamSeriesRow["archived_on"]

				let teamSeries = TeamSeries.Database(
					id: id,
					teamId: teamId,
					date: parseDate(from: date) ?? .now,
					archivedOn: archivedOn?.instantToDate
				)

				try teamSeries.insert(exportDb)
			}
		}

		private func parseDate(from date: String?) -> Date? {
			if let date {
				dateFormatter.date(from: date)
			} else {
				nil
			}
		}
	}
}

import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct TeamSeriesSeriesImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let teamSeriesSeriesRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT team_series_id, series_id, position FROM team_series_series;"
			)

			while let teamSeriesSeriesRow = try teamSeriesSeriesRows.next() {
				let teamSeriesId: TeamSeries.ID = teamSeriesSeriesRow["team_series_id"]
				let seriesId: Series.ID = teamSeriesSeriesRow["series_id"]
				let position: Int = teamSeriesSeriesRow["position"]

				let teamSeriesSeries = TeamSeriesSeries.Database(
					teamSeriesId: teamSeriesId,
					seriesId: seriesId,
					position: position
				)

				try teamSeriesSeries.insert(exportDb)
			}
		}
	}
}

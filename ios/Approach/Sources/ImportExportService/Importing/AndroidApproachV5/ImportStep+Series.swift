import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct SeriesImportStep: SQLiteImportStep {
		private let dateFormatter: DateFormatter = {
			let formatter = DateFormatter()
			formatter.locale = Locale(identifier: "en_US")
			formatter.dateFormat = "yyyy-MM-dd"
			return formatter
		}()

		func performImport(from importDb: Database, to exportDb: Database) throws {
			let seriesRows = try Row.fetchCursor(
				importDb,
				sql: """
					SELECT
						id,
						league_id,
						date,
						applied_date,
						pre_bowl,
						exclude_from_statistics,
						alley_id,
						archived_on
					FROM
						series;
				"""
			)

			while let seriesRow = try seriesRows.next() {
				let id: Series.ID = seriesRow["id"]
				let leagueId: League.ID = seriesRow["league_id"]
				let date: String = seriesRow["date"]
				let appliedDate: String? = seriesRow["applied_date"]
				let preBowl: String = seriesRow["pre_bowl"]
				let excludeFromStatistics: String = seriesRow["exclude_from_statistics"]
				let alleyId: Alley.ID? = seriesRow["alley_id"]
				let archivedOn: Double? = seriesRow["archived_on"]

				let series = Series.Database(
					leagueId: leagueId,
					id: id,
					date: parseDate(from: date) ?? .now,
					appliedDate: parseDate(from: appliedDate),
					preBowl: Series.PreBowl(rawValue: preBowl.snakeCaseToCamelCase) ?? .regular,
					excludeFromStatistics:
						Series.ExcludeFromStatistics(rawValue: excludeFromStatistics.snakeCaseToCamelCase) ?? .include,
					alleyId: alleyId,
					archivedOn: archivedOn?.instantToDate
				)

				try series.insert(exportDb)
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

import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct GamesImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let gameRows = try Row.fetchCursor(
				importDb,
				sql: """
					SELECT
						id,
						series_id,
						`index`,
						score,
						locked,
						scoring_method,
						exclude_from_statistics,
						archived_on,
						durationMillis
					FROM
						games;
				"""
			)

			while let gameRow = try gameRows.next() {
				let id: Game.ID = gameRow["id"]
				let seriesId: Series.ID = gameRow["series_id"]
				let index: Int = gameRow["index"]
				let score: Int = gameRow["score"]
				let locked: String = gameRow["locked"]
				let scoringMethod: String = gameRow["scoring_method"]
				let excludeFromStatistics: String = gameRow["exclude_from_statistics"]
				let archivedOn: Double? = gameRow["archived_on"]
				let durationMillis: Double = gameRow["durationMillis"]

				let game = Game.Database(
					seriesId: seriesId,
					id: id,
					index: index,
					score: score,
					locked: parseGameLocked(locked),
					scoringMethod: Game.ScoringMethod(rawValue: scoringMethod.snakeCaseToCamelCase) ?? .byFrame,
					excludeFromStatistics:
						Game.ExcludeFromStatistics(rawValue: excludeFromStatistics.snakeCaseToCamelCase) ?? .include,
					duration: durationMillis / 1000.0,
					archivedOn: archivedOn?.instantToDate
				)

				try game.insert(exportDb)
			}
		}

		private func parseGameLocked(_ locked: String) -> Game.Lock {
			switch locked {
			case "LOCKED": .locked
			case "UNLOCKED": .open
			default: .open
			}
		}
	}
}

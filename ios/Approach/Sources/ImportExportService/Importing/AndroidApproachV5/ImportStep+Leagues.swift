import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct LeaguesImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let leagueRows = try Row.fetchCursor(
				importDb,
				sql: """
					SELECT
						id,
						bowler_id,
						name,
						recurrence,
						number_of_games,
						additional_pin_fall,
						additional_games,
						exclude_from_statistics,
						archived_on
					FROM
						leagues;
				"""
			)

			while let leagueRow = try leagueRows.next() {
				let id: League.ID = leagueRow["id"]
				let bowlerId: Bowler.ID = leagueRow["bowler_id"]
				let name: String = leagueRow["name"]
				let recurrence: String = leagueRow["recurrence"]
				let numberOfGames: Int? = leagueRow["number_of_games"]
				let additionalPinFall: Int? = leagueRow["additional_pin_fall"]
				let additionalGames: Int? = leagueRow["additional_games"]
				let excludeFromStatistics: String = leagueRow["exclude_from_statistics"]
				let archivedOn: Double? = leagueRow["archived_on"]

				let league = League.Database(
					bowlerId: bowlerId,
					id: id,
					name: name,
					recurrence: League.Recurrence(rawValue: recurrence.snakeCaseToCamelCase) ?? .repeating,
					defaultNumberOfGames: numberOfGames,
					additionalPinfall: additionalPinFall,
					additionalGames: additionalGames,
					excludeFromStatistics:
						League.ExcludeFromStatistics(rawValue: excludeFromStatistics.snakeCaseToCamelCase) ?? .include,
					archivedOn: archivedOn?.instantToDate
				)

				try league.insert(exportDb)
			}
		}
	}
}

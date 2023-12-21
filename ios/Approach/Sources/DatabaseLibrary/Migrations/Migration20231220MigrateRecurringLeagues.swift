import Foundation
import GRDB

struct Migration20231220MigrateRecurringLeagues: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.execute(
			sql:
				"""
				UPDATE league
				SET recurrence = :repeatingRecurrence
				WHERE league.id IN (
					SELECT league.id
					FROM league
					LEFT JOIN series ON series.leagueId=league.id
					WHERE league.recurrence = :singleRecurrence
					GROUP BY league.id
					HAVING COUNT(series.id) >= :minNumberOfSeries
				)
				""",
			arguments: ["repeatingRecurrence": "repeating", "singleRecurrence": "once", "minNumberOfSeries": 2]
		)
	}
}

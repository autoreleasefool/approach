package ca.josephroque.bowlingcompanion.core.database.legacy.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeamBowler

suspend fun SQLiteMigrationManager.migrateTeamBowlers(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
			SELECT ${LegacyContract.TeamBowlerEntry.COLUMN_BOWLER_ID}, ${LegacyContract.TeamBowlerEntry.COLUMN_TEAM_ID}
			FROM ${LegacyContract.TeamBowlerEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val teamBowlers = mutableListOf<LegacyTeamBowler>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val bowlerIdIndex = cursor.getColumnIndex(LegacyContract.TeamBowlerEntry.COLUMN_BOWLER_ID)
			val teamIdIndex = cursor.getColumnIndex(LegacyContract.TeamBowlerEntry.COLUMN_TEAM_ID)

			if (bowlerIdIndex != -1 && teamIdIndex != -1) {
				val bowlerId = cursor.getLong(bowlerIdIndex)
				val teamId = cursor.getLong(teamIdIndex)

				teamBowlers.add(LegacyTeamBowler(teamId = teamId, bowlerId = bowlerId))
			}

			cursor.moveToNext()
		}
	}
	legacyMigrationRepository.migrateTeamBowlers(teamBowlers)
}
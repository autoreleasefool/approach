package ca.josephroque.bowlingcompanion.core.data.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.data.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam

suspend fun SQLiteMigrationManager.migrateTeams(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
		SELECT ${LegacyContract.TeamEntry._ID}, ${LegacyContract.TeamEntry.COLUMN_TEAM_NAME}
		FROM ${LegacyContract.TeamEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val teams = mutableListOf<LegacyTeam>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val teamIdIndex = cursor.getColumnIndex(LegacyContract.TeamEntry._ID)
			val teamNameIndex = cursor.getColumnIndex(LegacyContract.TeamEntry.COLUMN_TEAM_NAME)

			if (teamIdIndex != -1 && teamNameIndex != -1) {
				val id = cursor.getLong(teamIdIndex)
				val name = cursor.getString(teamNameIndex)

				teams.add(LegacyTeam(id = id, name = name))
			}

			cursor.moveToNext()
		}
	}

	cursor.close()

	legacyMigrationRepository.migrateTeams(teams)
}
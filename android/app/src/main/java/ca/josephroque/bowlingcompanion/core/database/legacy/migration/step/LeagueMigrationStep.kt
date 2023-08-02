package ca.josephroque.bowlingcompanion.core.database.legacy.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyLeague

suspend fun SQLiteMigrationManager.migrateLeagues(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
			SELECT 
				${LegacyContract.LeagueEntry._ID},
				${LegacyContract.LeagueEntry.COLUMN_LEAGUE_NAME},
				${LegacyContract.LeagueEntry.COLUMN_NUMBER_OF_GAMES},
				${LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_GAMES},
				${LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_PINFALL},
				${LegacyContract.LeagueEntry.COLUMN_IS_EVENT},
				${LegacyContract.LeagueEntry.COLUMN_BOWLER_ID}
			FROM ${LegacyContract.LeagueEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val leagues = mutableListOf<LegacyLeague>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val leagueIdIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry._ID)
			val leagueNameIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_LEAGUE_NAME)
			val leagueNumberOfGamesIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_NUMBER_OF_GAMES)
			val leagueAdditionalGamesIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_GAMES)
			val leagueAdditionalPinFallIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_PINFALL)
			val leagueIsEventIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_IS_EVENT)
			val leagueBowlerIdIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_BOWLER_ID)

			val id = cursor.getLong(leagueIdIndex)
			val name = cursor.getString(leagueNameIndex)
			val numberOfGames = cursor.getInt(leagueNumberOfGamesIndex)
			val additionalGames = cursor.getInt(leagueAdditionalGamesIndex)
			val additionalPinFall = cursor.getInt(leagueAdditionalPinFallIndex)
			val isEvent = cursor.getInt(leagueIsEventIndex) == 1
			val bowlerId = cursor.getLong(leagueBowlerIdIndex)

			leagues.add(LegacyLeague(
				id = id,
				name = name,
				isEvent = isEvent,
				gamesPerSeries = numberOfGames,
				additionalGames = additionalGames,
				additionalPinFall = additionalPinFall,
				bowlerId = bowlerId,
			))

			cursor.moveToNext()
		}
	}

	cursor.close()

	legacyMigrationRepository.migrateLeagues(leagues)
}
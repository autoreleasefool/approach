package ca.josephroque.bowlingcompanion.core.data.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.data.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyGame
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlayResult

suspend fun SQLiteMigrationManager.migrateGames(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
			SELECT
				${LegacyContract.GameEntry._ID},
				${LegacyContract.GameEntry.COLUMN_GAME_NUMBER},
				${LegacyContract.GameEntry.COLUMN_SCORE},
				${LegacyContract.GameEntry.COLUMN_IS_LOCKED},
				${LegacyContract.GameEntry.COLUMN_IS_MANUAL},
				${LegacyContract.GameEntry.COLUMN_MATCH_PLAY},
				${LegacyContract.GameEntry.COLUMN_SERIES_ID}
			FROM
				${LegacyContract.GameEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val games = mutableListOf<LegacyGame>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val gameIdIndex = cursor.getColumnIndex(LegacyContract.GameEntry._ID)
			val gameNumberIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_GAME_NUMBER)
			val gameScoreIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_SCORE)
			val gameIsLockedIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_IS_LOCKED)
			val gameIsManualIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_IS_MANUAL)
			val gameMatchPlayIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_MATCH_PLAY)
			val gameSeriesId = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_SERIES_ID)

			val id = cursor.getLong(gameIdIndex)
			val gameNumber = cursor.getInt(gameNumberIndex)
			val score = cursor.getInt(gameScoreIndex)
			val isLocked = cursor.getInt(gameIsLockedIndex) == 1
			val isManual = cursor.getInt(gameIsManualIndex) == 1
			val matchPlay = LegacyMatchPlayResult.fromInt(cursor.getInt(gameMatchPlayIndex)) ?: LegacyMatchPlayResult.NONE
			val seriesId = cursor.getLong(gameSeriesId)

			games.add(LegacyGame(
				id = id,
				gameNumber = gameNumber,
				score = score,
				isLocked = isLocked,
				isManual = isManual,
				matchPlayResult = matchPlay,
				seriesId = seriesId,
			))

			cursor.moveToNext()
		}
	}

	cursor.close()

	legacyMigrationRepository.migrateGames(games)
}
package ca.josephroque.bowlingcompanion.core.database.legacy.migration.step

import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlay

suspend fun SQLiteMigrationManager.migrateMatchPlays(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
			SELECT
				${LegacyContract.MatchPlayEntry._ID},
				${LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_NAME},
				${LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_SCORE},
				${LegacyContract.MatchPlayEntry.COLUMN_GAME_ID}
			FROM ${LegacyContract.MatchPlayEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val matchPlays = mutableListOf<LegacyMatchPlay>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val matchPlayIdIndex = cursor.getColumnIndex(LegacyContract.MatchPlayEntry._ID)
			val matchPlayOppNameIndex = cursor.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_NAME)
			val matchPlayOppScoreIndex = cursor.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_SCORE)
			val matchPlayGameId = cursor.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_GAME_ID)

			val id = cursor.getLong(matchPlayIdIndex)
			val opponentName = cursor.getStringOrNull(matchPlayOppNameIndex)
			val opponentScore = cursor.getInt(matchPlayOppScoreIndex)
			val gameId = cursor.getLong(matchPlayGameId)

			matchPlays.add(
				LegacyMatchPlay(
				id = id,
				opponentName = opponentName,
				opponentScore = opponentScore,
				gameId = gameId,
			)
			)

			cursor.moveToNext()
		}
	}

	cursor.close()
	legacyMigrationRepository.migrateMatchPlays(matchPlays)
}
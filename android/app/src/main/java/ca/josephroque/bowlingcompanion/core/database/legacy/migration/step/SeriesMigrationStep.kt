package ca.josephroque.bowlingcompanion.core.database.legacy.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacySeries
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun SQLiteMigrationManager.migrateSeries(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
			SELECT
				series.${LegacyContract.SeriesEntry._ID},
				series.${LegacyContract.SeriesEntry.COLUMN_SERIES_DATE},
				series.${LegacyContract.SeriesEntry.COLUMN_LEAGUE_ID},
				COUNT(*) AS numberOfGames
			FROM 
				${LegacyContract.SeriesEntry.TABLE_NAME} AS series,
				${LegacyContract.GameEntry.TABLE_NAME} AS game
			WHERE
				series.${LegacyContract.SeriesEntry._ID} = game.${LegacyContract.GameEntry.COLUMN_SERIES_ID}
			GROUP BY
				series.${LegacyContract.SeriesEntry._ID}
		""".trimIndent(), emptyArray()
	)

	val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)

	val series = mutableListOf<LegacySeries>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val seriesIdIndex = cursor.getColumnIndex(LegacyContract.SeriesEntry._ID)
			val seriesDateIndex = cursor.getColumnIndex(LegacyContract.SeriesEntry.COLUMN_SERIES_DATE)
			val seriesLeagueIdIndex = cursor.getColumnIndex(LegacyContract.SeriesEntry.COLUMN_LEAGUE_ID)
			val seriesNumberOfGamesIndex = cursor.getColumnIndex("numberOfGames")

			val id = cursor.getLong(seriesIdIndex)
			val leagueId = cursor.getLong(seriesLeagueIdIndex)
			val numberOfGames = cursor.getInt(seriesNumberOfGamesIndex)
			val date = dateFormatter.parse(cursor.getString(seriesDateIndex)) ?: Date()

			series.add(LegacySeries(
				id = id,
				date = date,
				numberOfGames = numberOfGames,
				leagueId = leagueId,
			))

			cursor.moveToNext()
		}
	}

	cursor.close()

	legacyMigrationRepository.migrateSeries(series)
}
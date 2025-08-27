
@file:Suppress("ktlint:standard:filename")

package ca.josephroque.bowlingcompanion.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ca.josephroque.bowlingcompanion.core.database.util.getUUID
import ca.josephroque.bowlingcompanion.core.database.util.toBlob
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID

val MIGRATION_6_7 = object : Migration(6, 7) {
	override fun migrate(db: SupportSQLiteDatabase) {
		val seriesIds = mutableListOf<SeriesID>()
		db.query(
			"""
					SELECT
						series.id as id
					FROM
						series
					INNER JOIN
						games on games.series_id = series.id
					WHERE
						games.archived_on IS NOT NULL
			""".trimIndent(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val idIndex = it.getColumnIndex("id")
					if (idIndex != -1) {
						seriesIds.add(SeriesID(it.getUUID(idIndex)))
					}

					it.moveToNext()
				}
			}
		}

		for (seriesId in seriesIds) {
			val gameIds = mutableListOf<GameID>()

			db.query(
				"""
						SELECT
							games.id as id
						FROM
							games
						WHERE
							games.series_id = ? AND games.archived_on IS NULL
						ORDER BY
							games.`index` ASC
				""".trimIndent(),
				arrayOf(seriesId.value.toBlob()),
			).use {
				if (it.moveToFirst()) {
					while (!it.isAfterLast) {
						val idIndex = it.getColumnIndex("id")
						if (idIndex != -1) {
							gameIds.add(GameID(it.getUUID(idIndex)))
						}

						it.moveToNext()
					}
				}
			}

			gameIds.forEachIndexed { index, id ->
				db.execSQL(
					"UPDATE games SET `index` = ? WHERE id = ?",
					arrayOf<Any>(index, id.value.toBlob()),
				)
			}
		}
	}
}

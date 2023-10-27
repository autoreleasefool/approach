package ca.josephroque.bowlingcompanion.core.data.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.data.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyBowler

suspend fun SQLiteMigrationManager.migrateBowlers(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
		SELECT ${LegacyContract.BowlerEntry._ID}, ${LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME}
		FROM ${LegacyContract.BowlerEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val bowlers = mutableListOf<LegacyBowler>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val bowlerIdIndex = cursor.getColumnIndex(LegacyContract.BowlerEntry._ID)
			val bowlerNameIndex = cursor.getColumnIndex(LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME)

			if (bowlerIdIndex != -1 && bowlerNameIndex != -1) {
				val id = cursor.getLong(bowlerIdIndex)
				val name = cursor.getString(bowlerNameIndex)

				bowlers.add(LegacyBowler(id = id, name = name))
			}

			cursor.moveToNext()
		}
	}

	cursor.close()

	legacyMigrationRepository.migrateBowlers(bowlers)
}
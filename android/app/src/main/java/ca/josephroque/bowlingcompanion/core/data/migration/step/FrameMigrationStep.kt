package ca.josephroque.bowlingcompanion.core.data.migration.step

import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.data.migration.SQLiteMigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyFrame

suspend fun SQLiteMigrationManager.migrateFrames(legacyDb: SQLiteDatabase) {
	val cursor = legacyDb.rawQuery(
		"""
			SELECT
				${LegacyContract.FrameEntry._ID},
				${LegacyContract.FrameEntry.COLUMN_FRAME_NUMBER},
				${LegacyContract.FrameEntry.COLUMN_IS_ACCESSED},
				${LegacyContract.FrameEntry.COLUMN_PIN_STATE[0]},
				${LegacyContract.FrameEntry.COLUMN_PIN_STATE[1]},
				${LegacyContract.FrameEntry.COLUMN_PIN_STATE[2]},
				${LegacyContract.FrameEntry.COLUMN_FOULS},
				${LegacyContract.FrameEntry.COLUMN_GAME_ID}
			FROM ${LegacyContract.FrameEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
	)

	val frames = mutableListOf<LegacyFrame>()
	if (cursor.moveToFirst()) {
		while (!cursor.isAfterLast) {
			val frameIdIndex = cursor.getColumnIndex(LegacyContract.FrameEntry._ID)
			val frameNumberIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_FRAME_NUMBER)
			val frameIsAccessedIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_IS_ACCESSED)
			val framePinState0Index = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[0])
			val framePinState1Index = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[1])
			val framePinState2Index = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[2])
			val frameFoulsIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_FOULS)
			val frameGameIdIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_GAME_ID)

			val id = cursor.getLong(frameIdIndex)
			val frameNumber = cursor.getInt(frameNumberIndex)
			val isAccessed = cursor.getInt(frameIsAccessedIndex) == 1
			val pinState0 = cursor.getInt(framePinState0Index)
			val pinState1 = cursor.getInt(framePinState1Index)
			val pinState2 = cursor.getInt(framePinState2Index)
			val fouls = cursor.getInt(frameFoulsIndex)
			val gameId = cursor.getLong(frameGameIdIndex)

			frames.add(LegacyFrame(
				id = id,
				ordinal = frameNumber,
				isAccessed = isAccessed,
				firstPinState = pinState0,
				secondPinState = pinState1,
				thirdPinState = pinState2,
				fouls = fouls,
				gameId = gameId,
			))

			cursor.moveToNext()
		}
	}

	cursor.close()
	legacyMigrationRepository.migrateFrames(frames)
}
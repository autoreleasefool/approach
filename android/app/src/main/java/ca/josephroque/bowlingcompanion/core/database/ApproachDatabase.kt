package ca.josephroque.bowlingcompanion.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity

@Database(
	entities = [
		BowlerEntity::class,
	],
	version = 1,
)
abstract class ApproachDatabase : RoomDatabase() {
	abstract fun bowlerDao(): BowlerDao
}
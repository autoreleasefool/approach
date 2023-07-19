package ca.josephroque.bowlingcompanion.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.util.BowlerKindConverter

@Database(
	entities = [
		BowlerEntity::class,
	],
	version = 1,
)
@TypeConverters(
	BowlerKindConverter::class,
)
abstract class ApproachDatabase : RoomDatabase() {
	abstract fun bowlerDao(): BowlerDao
}
package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface LegacyMigratingDao<T> {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun migrateAll(entities: Collection<T>)
}
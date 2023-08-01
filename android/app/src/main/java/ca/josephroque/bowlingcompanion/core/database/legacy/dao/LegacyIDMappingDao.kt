package ca.josephroque.bowlingcompanion.core.database.legacy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity

@Dao
interface LegacyIDMappingDao {
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertAll(entities: List<LegacyIDMappingEntity>)
}
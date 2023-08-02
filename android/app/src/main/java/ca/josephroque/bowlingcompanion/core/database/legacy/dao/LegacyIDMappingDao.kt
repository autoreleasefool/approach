package ca.josephroque.bowlingcompanion.core.database.legacy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingKey

@Dao
interface LegacyIDMappingDao {
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertAll(entities: List<LegacyIDMappingEntity>)

	@Query("""
		SELECT * FROM legacy_ids
		WHERE mapping_key = :key
		AND legacy_id IN (:legacyIds)
	""")
	suspend fun getLegacyIDMappings(legacyIds: List<Long>, key: LegacyIDMappingKey): List<LegacyIDMappingEntity>
}
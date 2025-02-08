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

	@Query(
		"""
		SELECT * FROM legacy_ids
		WHERE mapping_key = :key
		AND legacy_id IN (:legacyIds)
		ORDER BY legacy_id
		LIMIT :limit
		OFFSET :offset
	""",
	)
	suspend fun getLegacyIDMappings(
		legacyIds: List<Long>,
		key: LegacyIDMappingKey,
		limit: Int,
		offset: Int,
	): List<LegacyIDMappingEntity>

	suspend fun getLegacyIDMappings(legacyIds: List<Long>, key: LegacyIDMappingKey): List<LegacyIDMappingEntity> {
		val limit = 100
		val results = mutableListOf<LegacyIDMappingEntity>()
		var offset = 0

		var result = getLegacyIDMappings(legacyIds, key, limit, offset)
		while (result.isNotEmpty()) {
			results.addAll(result)
			offset += limit
			result = getLegacyIDMappings(legacyIds, key, limit, offset)
		}

		return results
	}
}

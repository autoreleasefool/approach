package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.LaneEntity
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class LaneDao {
	@Query(
		"""
			SELECT
				lanes.id AS id,
				lanes.label AS label,
				lanes.position AS position
			FROM lanes
			WHERE lanes.id IN (:ids)
			ORDER BY lanes.label
		"""
	)
	abstract fun getLanes(ids: List<UUID>): Flow<List<LaneListItem>>

	@Query("DELETE FROM lanes WHERE alley_id = :alleyId")
	abstract fun deleteAlleyLanes(alleyId: UUID)

	@Insert
	abstract fun insertAll(lanes: List<LaneEntity>)
}
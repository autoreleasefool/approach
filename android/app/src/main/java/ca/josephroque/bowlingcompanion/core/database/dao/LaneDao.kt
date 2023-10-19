package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ca.josephroque.bowlingcompanion.core.database.model.LaneCreate
import ca.josephroque.bowlingcompanion.core.database.model.LaneEntity
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

abstract class LaneDao: BaseDao<LaneEntity> {
	@Query(
		"""
			SELECT
				lanes.alley_id AS alleyId,
				lanes.id AS id,
				lanes.label AS label,
				lanes.position AS position
			FROM lanes
			WHERE lanes.alley_id = :alleyId
		"""
	)
	abstract fun getAlleyLanes(alleyId: UUID): Flow<List<LaneListItem>>

	@Transaction
	fun overwriteAlleyLanes(alleyId: UUID, lanes: List<LaneCreate>) {
		deleteAlleyLanes(alleyId)
		insertAll(lanes)
	}

	@Query("DELETE FROM lanes WHERE alley_id = :alleyId")
	abstract fun deleteAlleyLanes(alleyId: UUID)

	@Insert(entity = LaneEntity::class)
	abstract fun insertAll(lanes: List<LaneCreate>)
}
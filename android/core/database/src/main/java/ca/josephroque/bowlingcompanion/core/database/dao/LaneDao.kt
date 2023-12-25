package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.GameLaneCrossRef
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

	@Query(
		"""
			SELECT
				lanes.id AS id,
				lanes.label AS label,
				lanes.position AS position
			FROM lanes
			WHERE lanes.alley_id = :alleyId
			ORDER BY lanes.label
		"""
	)
	abstract fun getAlleyLanes(alleyId: UUID): Flow<List<LaneListItem>>

	@Query(
		"""
			SELECT
				lanes.id AS id,
				lanes.label AS label,
				lanes.position AS position
			FROM lanes
			JOIN game_lanes ON game_lanes.lane_id = lanes.id
			WHERE game_lanes.game_id = :gameId
			ORDER BY lanes.label
		"""
	)
	abstract fun getGameLanes(gameId: UUID): Flow<List<LaneListItem>>

	@Query("DELETE FROM lanes WHERE alley_id = :alleyId")
	abstract fun deleteAlleyLanes(alleyId: UUID)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract fun insertAll(lanes: List<LaneEntity>)
}
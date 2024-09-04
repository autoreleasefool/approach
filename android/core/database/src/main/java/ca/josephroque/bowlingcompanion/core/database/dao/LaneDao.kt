package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.LaneEntity
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow

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
			ORDER BY lanes.label * 1 ASC
		""",
	)
	abstract fun getLanes(ids: List<LaneID>): Flow<List<LaneListItem>>

	@Query(
		"""
			SELECT
				lanes.id AS id,
				lanes.label AS label,
				lanes.position AS position
			FROM lanes
			WHERE lanes.alley_id = :alleyId
			ORDER BY lanes.label * 1 ASC
		""",
	)
	abstract fun getAlleyLanes(alleyId: AlleyID): Flow<List<LaneListItem>>

	@Query(
		"""
			SELECT
				lanes.id AS id,
				lanes.label AS label,
				lanes.position AS position
			FROM lanes
			JOIN game_lanes ON game_lanes.lane_id = lanes.id
			WHERE game_lanes.game_id = :gameId
			ORDER BY lanes.label * 1 ASC
		""",
	)
	abstract fun getGameLanes(gameId: GameID): Flow<List<LaneListItem>>

	@Query("DELETE FROM lanes WHERE alley_id = :alleyId")
	abstract fun deleteAlleyLanes(alleyId: AlleyID)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract fun insertAll(lanes: List<LaneEntity>)
}

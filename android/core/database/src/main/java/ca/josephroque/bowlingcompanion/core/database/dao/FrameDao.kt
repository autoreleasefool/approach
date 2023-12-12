package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.FrameEditEntity
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.ScoreableFrameEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class FrameDao: LegacyMigratingDao<FrameEntity> {
	@Transaction
	@Query(
		"""
			SELECT
				frames.game_id AS gameId,
				frames.`index` AS `index`,
				frames.roll0 AS roll0,
				frames.roll1 AS roll1,
				frames.roll2 AS roll2,
				ball0.id AS ball0_id,
				ball0.kind AS ball0_kind,
				ball0.name AS ball0_name,
				ball0.avatar AS ball0_avatar,
				ball1.id AS ball1_id,
				ball1.kind AS ball1_kind,
				ball1.name AS ball1_name,
				ball1.avatar AS ball1_avatar,
				ball2.id AS ball2_id,
				ball2.kind AS ball2_kind,
				ball2.name AS ball2_name,
				ball2.avatar AS ball2_avatar
			FROM frames
			LEFT JOIN gear AS ball0
				ON ball0.id = frames.ball0
			LEFT JOIN gear AS ball1
				ON ball1.id = frames.ball1
			LEFT JOIN gear AS ball2
				ON ball2.id = frames.ball2
			WHERE frames.game_id = :gameId
			ORDER BY frames.`index` ASC
		"""
	)
	abstract fun getFrames(gameId: UUID): Flow<List<FrameEditEntity>>

	@Query(
		"""
			SELECT
				frames.`index` AS `index`,
				frames.roll0 AS roll0,
				frames.roll1 AS roll1,
				frames.roll2 AS roll2
			FROM frames
			WHERE frames.game_id = :gameId
			ORDER BY frames.`index` ASC
		"""
	)
	abstract fun getScoreableFrames(gameId: UUID): Flow<List<ScoreableFrameEntity>>

	@Update
	abstract fun updateFrame(frame: FrameEntity)
}
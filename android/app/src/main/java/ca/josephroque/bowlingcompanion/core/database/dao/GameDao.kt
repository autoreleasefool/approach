package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class GameDao: BaseDao<GameEntity> {
	@Query(
		"""
			SELECT
			 games.id,
			 games.`index`,
			 games.score
			FROM games
			WHERE games.series_id = :seriesId
		"""
	)
	abstract fun getGamesList(seriesId: UUID): Flow<List<GameListItem>>
}
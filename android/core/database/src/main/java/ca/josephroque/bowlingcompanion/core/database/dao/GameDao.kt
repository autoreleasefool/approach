package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameDetails
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class GameDao: BaseDao<GameEntity> {
	@Query(
		"""
			SELECT
				games.id AS id,
				games.`index` AS `index`,
				games.score AS score,
				games.locked AS locked,
				games.scoring_method AS scoringMethod,
				games.exclude_from_statistics AS excludeFromStatistics,
				series.`date` AS series_date,
				series.pre_bowl AS series_preBowl,
				series.exclude_from_statistics AS series_excludeFromStatistics,
				leagues.name AS league_name,
				leagues.exclude_from_statistics AS league_excludeFromStatistics,
				bowlers.name AS bowler_name
			FROM games
			JOIN series ON series.id = games.series_id
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE games.id = :gameId
		"""
	)
	abstract fun getGameDetails(gameId: UUID): Flow<GameDetails>

	@Query(
		"""
			SELECT
			 games.id,
			 games.`index`,
			 games.score
			FROM games
			WHERE games.series_id = :seriesId
			ORDER BY games.`index`
		"""
	)
	abstract fun getGamesList(seriesId: UUID): Flow<List<GameListItem>>
}
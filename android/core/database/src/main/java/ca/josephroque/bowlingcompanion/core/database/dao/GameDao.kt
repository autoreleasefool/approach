package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEditEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class GameDao: LegacyMigratingDao<GameEntity> {
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
	abstract fun getGameDetails(gameId: UUID): Flow<GameEditEntity>

	@Query(
		"""
			SELECT
			 games.id,
			 games.`index`,
			 games.score
			FROM games
			WHERE games.series_id = :seriesId AND games.archived_on IS NULL
			ORDER BY games.`index`
		"""
	)
	abstract fun getGamesList(seriesId: UUID): Flow<List<GameListItem>>

	@Query(
		"""
			SELECT
				games.`index`
			FROM games
			WHERE games.id = :gameId
		"""
	)
	abstract fun getGameIndex(gameId: UUID): Flow<Int>

	@Query(
		"""
			SELECT
				games.id AS id,
				games.score AS score,
				games.scoring_method AS scoringMethod,
				games.archived_on AS archivedOn,
				series.date AS seriesDate,
				bowlers.name AS bowlerName,
				leagues.name AS leagueName
			FROM games
			JOIN series ON series.id = games.series_id
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE games.archived_on IS NOT NULL
			ORDER BY games.archived_on DESC
		"""
	)
	abstract fun getArchivedGames(): Flow<List<ArchivedGame>>

	@Query("UPDATE games SET archived_on = NULL WHERE id = :gameId")
	abstract fun unarchiveGame(gameId: UUID)

	@Query("UPDATE games SET archived_on = NULL WHERE id = :gameId")
	abstract fun archiveGame(gameId: UUID)
}
package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.GameEditEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameLaneCrossRef
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.GameListItemBySeries
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Dao
abstract class GameDao : LegacyMigratingDao<GameEntity> {
	@Query(
		"""
			SELECT
				games.id AS id,
				games.`index` AS `index`,
				games.score AS score,
				games.locked AS locked,
				games.scoring_method AS scoringMethod,
				games.exclude_from_statistics AS excludeFromStatistics,
				games.durationMillis AS durationMillis,
				series.id AS series_id,
				series.`date` AS series_date,
				series.pre_bowl AS series_preBowl,
				series.exclude_from_statistics AS series_excludeFromStatistics,
				leagues.id AS league_id,
				leagues.name AS league_name,
				leagues.exclude_from_statistics AS league_excludeFromStatistics,
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name
			FROM games
			JOIN series ON series.id = games.series_id
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE games.id = :gameId
		""",
	)
	abstract fun getGameDetails(gameId: GameID): Flow<GameEditEntity>

	@Query(
		"""
			SELECT
			 games.id,
			 games.`index`,
			 games.score
			FROM games
			WHERE games.series_id = :seriesId AND games.archived_on IS NULL
			ORDER BY games.`index` ASC
		""",
	)
	abstract fun getGamesList(seriesId: SeriesID): Flow<List<GameListItem>>

	@Query(
		"""
			SELECT
			  games.series_id as seriesId,
				games.id,
				games.`index`
			FROM games
			WHERE games.series_id IN (:series) AND games.`index` = :gameIndex AND games.archived_on IS NULL
		""",
	)
	abstract fun getGamesFromSeries(
		series: List<SeriesID>,
		gameIndex: Int,
	): Flow<List<GameListItemBySeries>>

	@Query(
		"""
			SELECT
				games.id
			FROM games
			WHERE games.series_id = :seriesId AND games.archived_on IS NULL
			ORDER BY games.`index` ASC
		""",
	)
	abstract fun getGameIds(seriesId: SeriesID): Flow<List<GameID>>

	@Query(
		"""
			SELECT
				games.id
			FROM games
			JOIN team_series_series
				ON team_series_series.series_id = games.series_id
			WHERE team_series_series.team_series_id = :teamSeriesId
			ORDER BY team_series_series.position ASC
		""",
	)
	abstract fun getTeamSeriesGameIds(teamSeriesId: TeamSeriesID): Flow<List<GameID>>

	@Query(
		"""
			SELECT
				games.`index`
			FROM games
			WHERE games.id = :gameId
		""",
	)
	abstract fun getGameIndex(gameId: GameID): Flow<Int>

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
		""",
	)
	abstract fun getArchivedGames(): Flow<List<ArchivedGame>>

	@Query("UPDATE games SET scoring_method = :scoringMethod, score = :score WHERE id = :gameId")
	abstract fun setGameScoringMethod(gameId: GameID, scoringMethod: GameScoringMethod, score: Int)

	@Query("UPDATE games SET locked = :locked WHERE id = :gameId")
	abstract fun setGameLockState(gameId: GameID, locked: GameLockState)

	@Query("UPDATE games SET score = :score WHERE id = :gameId")
	abstract fun setGameScore(gameId: GameID, score: Int)

	@Query("UPDATE games SET durationMillis = :durationMillis WHERE id = :gameId")
	abstract fun setGameDuration(gameId: GameID, durationMillis: Long)

	@Query("UPDATE games SET exclude_from_statistics = :excludeFromStatistics WHERE id = :gameId")
	abstract fun setGameExcludedFromStatistics(
		gameId: GameID,
		excludeFromStatistics: ExcludeFromStatistics,
	)

	@Query("DELETE FROM game_lanes WHERE game_id = :gameId")
	abstract fun deleteGameLanes(gameId: GameID)

	@Insert
	abstract fun insertGameLanes(gameLanes: List<GameLaneCrossRef>)

	@Query("UPDATE games SET archived_on = NULL WHERE id = :gameId")
	abstract fun unarchiveGame(gameId: GameID)

	@Query("UPDATE games SET archived_on = :archivedOn WHERE id = :gameId")
	abstract fun archiveGame(gameId: GameID, archivedOn: Instant)

	@Insert
	abstract fun insertGames(games: List<GameEntity>)

	@Query(
		"""
			UPDATE games
			SET locked = 'LOCKED'
			WHERE 
				games.locked = 'UNLOCKED' AND
				games.series_id IN (
					SELECT series.id
					FROM series
					WHERE series.date < :cutOffDate
				)
		""",
	)
	abstract fun lockStaleGames(cutOffDate: LocalDate)
}

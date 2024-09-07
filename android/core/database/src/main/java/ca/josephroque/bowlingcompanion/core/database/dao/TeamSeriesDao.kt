package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesDetailItemEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesSeriesCrossRef
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TeamSeriesDao {
	@Query(
		"""
			SELECT
				team_series.id AS id,
				team_series.`date` AS `date`,
				SUM(games.score) AS total
			FROM team_series
			INNER JOIN team_series_series
				ON team_series_series.team_series_id = team_series.id
			INNER JOIN series
				ON series.id = team_series_series.series_id AND series.archived_on IS NULL
			INNER JOIN games
				ON games.series_id = series.id AND games.archived_on IS NULL
			WHERE team_series.team_id = :teamId
			GROUP BY team_series.id
			ORDER BY
			CASE WHEN :sortOrder = 'OLDEST_TO_NEWEST' THEN team_series.`date` END ASC,
			CASE WHEN :sortOrder = 'NEWEST_TO_OLDEST' THEN team_series.`date` END DESC,
			CASE WHEN :sortOrder = 'HIGHEST_TO_LOWEST' THEN total END DESC,
			CASE WHEN :sortOrder = 'LOWEST_TO_HIGHEST' THEN total END ASC
		""",
	)
	abstract fun getTeamSeriesList(
		teamId: TeamID,
		sortOrder: TeamSeriesSortOrder,
	): Flow<List<TeamSeriesSummary>>

	@Query(
		"""
			SELECT
				team_series.`date` AS `date`,
				games.score AS score,
				games.`index` AS game_index,
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name
			FROM team_series
			INNER JOIN team_series_series
				ON team_series_series.team_series_id = team_series.id
			INNER JOIN series
				ON series.id = team_series_series.series_id AND series.archived_on IS NULL
			INNER JOIN games
				ON games.series_id = series.id AND games.archived_on IS NULL
			INNER JOIN leagues
				ON leagues.id = series.league_id
			INNER JOIN bowlers
				ON bowlers.id = leagues.bowler_id
			WHERE team_series.id = :teamSeriesId
			ORDER BY team_series_series.position ASC, games.`index` ASC
		""",
	)
	abstract fun getTeamSeriesDetails(teamSeriesId: TeamSeriesID): List<TeamSeriesDetailItemEntity>

	@Query(
		"""
			DELETE FROM team_series_series
			WHERE team_series_id = :teamSeriesId
		""",
	)
	abstract fun deleteSeries(teamSeriesId: TeamSeriesID)

	@Insert(entity = TeamSeriesEntity::class)
	abstract fun insertSeries(series: TeamSeriesCreateEntity)

	@Insert
	abstract fun insertAll(series: List<TeamSeriesSeriesCrossRef>)
}

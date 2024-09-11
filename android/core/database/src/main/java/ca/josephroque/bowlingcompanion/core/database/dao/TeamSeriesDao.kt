package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesDetailItemEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesSeriesCrossRef
import ca.josephroque.bowlingcompanion.core.model.ArchivedTeamSeries
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

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
				games.id AS game_id,
				games.score AS score,
				games.`index` AS game_index,
				games.archived_on IS NOT NULL AS game_is_archived,
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name
			FROM team_series
			INNER JOIN team_series_series
				ON team_series_series.team_series_id = team_series.id
			INNER JOIN series
				ON series.id = team_series_series.series_id AND series.archived_on IS NULL
			INNER JOIN games
				ON games.series_id = series.id
			INNER JOIN leagues
				ON leagues.id = series.league_id
			INNER JOIN bowlers
				ON bowlers.id = leagues.bowler_id
			WHERE team_series.id = :teamSeriesId
			ORDER BY team_series_series.position ASC, games.`index` ASC
		""",
	)
	abstract fun getTeamSeriesDetails(
		teamSeriesId: TeamSeriesID,
	): Flow<List<TeamSeriesDetailItemEntity>>

	@Query(
		"""
			SELECT
			  team_series.id AS id,
				team_series.`date` AS `date`,
				team_series.archived_on AS archivedOn,
				teams.name AS teamName
			FROM team_series
			JOIN team_series_series ON team_series_series.team_series_id = team_series.id
			JOIN series ON series.id = team_series_series.series_id
			JOIN teams ON teams.id = team_series.team_id
			WHERE team_series.archived_on IS NOT NULL
			GROUP BY team_series.id
			ORDER BY team_series.archived_on DESC
		""",
	)
	abstract fun getArchivedTeamSeries(): Flow<List<ArchivedTeamSeries>>

	@Query(
		"""
			DELETE FROM team_series_series
			WHERE team_series_id = :teamSeriesId
		""",
	)
	abstract fun deleteSeries(teamSeriesId: TeamSeriesID)

	@Query("UPDATE team_series SET `date` = :date WHERE id = :teamSeriesId")
	abstract fun setTeamSeriesDate(teamSeriesId: TeamSeriesID, date: LocalDate)

	@Insert(entity = TeamSeriesEntity::class)
	abstract fun insertSeries(series: TeamSeriesCreateEntity)

	@Insert
	abstract fun insertAll(series: List<TeamSeriesSeriesCrossRef>)

	@Query("UPDATE team_series SET archived_on = :archivedOn WHERE id = :teamSeriesId")
	abstract fun archiveSeries(teamSeriesId: TeamSeriesID, archivedOn: Instant)

	@Query("UPDATE team_series SET archived_on = NULL WHERE id = :teamSeriesId")
	abstract fun unarchiveSeries(teamSeriesId: TeamSeriesID)
}

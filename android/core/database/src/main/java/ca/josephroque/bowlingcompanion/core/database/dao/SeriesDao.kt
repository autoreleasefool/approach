package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.SeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesDetailsEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Dao
abstract class SeriesDao : LegacyMigratingDao<SeriesEntity> {
	@Transaction
	@Query(
		"""
			SELECT
				series.league_id AS leagueId,
				series.alley_id AS alleyId,
				series.id AS id,
				series.'date' AS 'date',
				series.applied_date AS appliedDate,
				series.pre_bowl AS preBowl,
				series.exclude_from_statistics AS excludeFromStatistics,
				COUNT(games.id) AS numberOfGames,
				SUM(games.score) AS 'total'
			FROM series
			LEFT JOIN games
				ON games.series_id = series.id AND games.archived_on IS NULL
			WHERE series.id = :seriesId
		""",
	)
	abstract fun getSeriesDetails(seriesId: SeriesID): Flow<SeriesDetailsEntity>

	@Transaction
	@Query(
		"""
		SELECT
		 series.id AS id,
		 series.'date' AS 'date',
		 series.applied_date AS appliedDate,
		 series.pre_bowl AS preBowl,
		 SUM(games.score) AS 'total',
		 COALESCE(series.applied_date, series.'date') AS orderingDate
		FROM series
		LEFT JOIN games
			ON games.series_id = series.id AND games.archived_on IS NULL
		WHERE series.league_id = :leagueId
			AND series.archived_on IS NULL
			AND (
				:preBowl IS NULL
				OR series.pre_bowl = :preBowl
			)
		GROUP BY series.id
		ORDER BY
		CASE WHEN :seriesSortOrder = 'OLDEST_TO_NEWEST' THEN orderingDate END ASC,
		CASE WHEN :seriesSortOrder = 'NEWEST_TO_OLDEST' THEN orderingDate END DESC,
		CASE WHEN :seriesSortOrder = 'HIGHEST_TO_LOWEST' THEN total END DESC,
		CASE WHEN :seriesSortOrder = 'LOWEST_TO_HIGHEST' THEN total END ASC
		""",
	)
	abstract fun getSeriesList(
		leagueId: LeagueID,
		seriesSortOrder: SeriesSortOrder,
		preBowl: SeriesPreBowl?,
	): Flow<List<SeriesListEntity>>

	@Query(
		"""
			SELECT *
			FROM series
			WHERE series.league_id IN (:eventIds)
		""",
	)
	abstract fun getEventSeriesList(eventIds: List<LeagueID>): Flow<List<SeriesEntity>>

	@Query(
		"""
			SELECT series_id
			FROM team_series_series
			WHERE team_series_id = :teamSeriesId
			ORDER BY position ASC
		""",
	)
	abstract fun getTeamSeriesIds(teamSeriesId: TeamSeriesID): Flow<List<SeriesID>>

	@Query(
		"""
			SELECT
				series.id AS id,
				series.'date' AS 'date',
				series.archived_on AS archivedOn,
				bowlers.name AS bowlerName,
				leagues.name AS leagueName,
				COUNT(games.id) AS numberOfGames
			FROM series
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			LEFT JOIN games ON games.series_id = series.id
			WHERE series.archived_on IS NOT NULL
			GROUP BY series.id
			ORDER BY series.archived_on DESC
		""",
	)
	abstract fun getArchivedSeries(): Flow<List<ArchivedSeries>>

	@Query("UPDATE series SET alley_id = :alleyId WHERE id = :seriesId")
	abstract fun setSeriesAlley(seriesId: SeriesID, alleyId: AlleyID?)

	@Insert(entity = SeriesEntity::class)
	abstract fun insertSeries(series: SeriesCreateEntity)

	@Update(entity = SeriesEntity::class)
	abstract fun updateSeries(series: SeriesUpdateEntity)

	@Query("UPDATE series SET archived_on = :archivedOn WHERE id = :seriesId")
	abstract fun archiveSeries(seriesId: SeriesID, archivedOn: Instant)

	@Query(
		"""
			UPDATE series SET archived_on = :archivedOn WHERE id IN (
				SELECT series.id
				FROM series
				JOIN team_series_series ON team_series_series.series_id = series.id
				WHERE team_series_series.team_series_id = :teamSeriesId
			)
		""",
	)
	abstract fun archiveSeries(teamSeriesId: TeamSeriesID, archivedOn: Instant)

	@Query(
		"""
			UPDATE series SET archived_on = NULL WHERE id IN (
				SELECT series.id
				FROM series
				JOIN team_series_series ON team_series_series.series_id = series.id
				WHERE team_series_series.team_series_id = :teamSeriesId
			)
		""",
	)
	abstract fun unarchiveSeries(teamSeriesId: TeamSeriesID)

	@Query("UPDATE series SET archived_on = NULL WHERE id = :seriesId")
	abstract fun unarchiveSeries(seriesId: SeriesID)

	@Query(
		"""
			UPDATE series 
			SET 
				exclude_from_statistics = 'INCLUDE',
				applied_date = :appliedDate
			WHERE id = :seriesId
		""",
	)
	abstract fun usePreBowl(seriesId: SeriesID, appliedDate: LocalDate)
}

package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesDetailsEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.util.UUID

@Dao
abstract class SeriesDao: LegacyMigratingDao<SeriesEntity> {
	@Transaction
	@Query(
		"""
			SELECT
				series.league_id AS leagueId,
				series.alley_id AS alleyId,
				series.id AS id,
				series."date" AS "date",
				series.pre_bowl AS preBowl,
				series.exclude_from_statistics AS excludeFromStatistics,
				COUNT(games.id) AS numberOfGames,
				SUM(games.score) AS "total"
			FROM series
			LEFT JOIN games
				ON games.series_id = series.id AND games.archived_on IS NULL
			WHERE series.id = :seriesId
		"""
	)
	abstract fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetailsEntity>

	@Transaction
	@Query(
		"""
		SELECT
		 series.id AS id,
		 series."date" AS "date",
		 series.pre_bowl AS preBowl,
		 SUM(games.score) AS "total"
		FROM series
		LEFT JOIN games
			ON games.series_id = series.id AND games.archived_on IS NULL
		WHERE series.league_id = :leagueId
		GROUP BY series.id
		ORDER BY
		CASE WHEN :seriesSortOrder = "OLDEST_TO_NEWEST" THEN series."date" END ASC,
		CASE WHEN :seriesSortOrder = "NEWEST_TO_OLDEST" THEN series."date" END DESC,
		CASE WHEN :seriesSortOrder = "HIGHEST_TO_LOWEST" THEN total END DESC,
		CASE WHEN :seriesSortOrder = "LOWEST_TO_HIGHEST" THEN total END ASC
		"""
	)
	abstract fun getSeriesList(leagueId: UUID, seriesSortOrder: SeriesSortOrder): Flow<List<SeriesListEntity>>

	@Query(
		"""
			SELECT
				series.id AS id,
				series."date" AS "date",
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
		"""
	)
	abstract fun getArchivedSeries(): Flow<List<ArchivedSeries>>

	@Insert(entity = SeriesEntity::class)
	abstract fun insertSeries(series: SeriesCreateEntity)

	@Update(entity = SeriesEntity::class)
	abstract fun updateSeries(series: SeriesUpdateEntity)

	@Query("UPDATE series SET archived_on = :archivedOn WHERE id = :seriesId")
	abstract fun archiveSeries(seriesId: UUID, archivedOn: Instant)

	@Query("UPDATE series SET archived_on = NULL WHERE id = :seriesId")
	abstract fun unarchiveSeries(seriesId: UUID)
}
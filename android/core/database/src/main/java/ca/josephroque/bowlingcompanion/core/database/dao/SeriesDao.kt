package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.database.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.database.model.SeriesUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class SeriesDao: LegacyMigratingDao<SeriesEntity> {
	@Transaction
	@Query(
		"""
			SELECT
				series.id AS id,
				series."date" AS "date",
				series.pre_bowl AS preBowl,
				series.exclude_from_statistics AS excludeFromStatistics,
				series.number_of_games AS numberOfGames,
				SUM(games.score) AS "total"
			FROM series
			LEFT JOIN games
				ON games.series_id = series.id
				AND games.score > 0
			WHERE series.id = :seriesId
		"""
	)
	abstract fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetails>

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
			ON games.series_id = series.id
			AND games.score > 0
		WHERE series.league_id = :leagueId
		GROUP BY series.id
		ORDER BY series."date" DESC
		"""
	)
	abstract fun getSeriesList(leagueId: UUID): Flow<List<SeriesListItem>>

	@Insert(entity = SeriesEntity::class)
	abstract fun insertSeries(series: SeriesCreate)

	@Update(entity = SeriesEntity::class)
	abstract fun updateSeries(series: SeriesUpdate)

	@Query("DELETE FROM series WHERE id = :seriesId")
	abstract fun deleteSeries(seriesId: UUID)
}
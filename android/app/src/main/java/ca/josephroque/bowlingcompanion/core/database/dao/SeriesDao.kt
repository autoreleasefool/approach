package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class SeriesDao: BaseDao<SeriesEntity> {
	@Transaction
	@Query(
		"""
			SELECT
				series.id AS id,
				series."date" AS "date",
				series.pre_bowl AS pre_bowl,
				series.exclude_from_statistics AS exclude_from_statistics,
				series.number_of_games as number_of_games,
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
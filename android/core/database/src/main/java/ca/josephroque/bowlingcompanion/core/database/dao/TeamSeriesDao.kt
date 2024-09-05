package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesSeriesCrossRef
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID

@Dao
abstract class TeamSeriesDao {
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

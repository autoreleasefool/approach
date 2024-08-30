package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesSeriesCrossRef

@Dao
abstract class TeamSeriesDao {
	@Insert(entity = TeamSeriesEntity::class)
	abstract fun insertSeries(series: TeamSeriesCreateEntity)

	@Insert
	abstract fun insertAll(series: List<TeamSeriesSeriesCrossRef>)
}

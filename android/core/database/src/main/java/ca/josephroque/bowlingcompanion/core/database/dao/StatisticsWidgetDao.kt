package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.StatisticsWidgetCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.StatisticsWidgetEntity
import ca.josephroque.bowlingcompanion.core.database.model.StatisticsWidgetPriorityUpdateEntity
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetID
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StatisticsWidgetDao {
	@Query(
		"""
			SELECT
				statistics_widget.id,
				statistics_widget.bowler_id,
				statistics_widget.league_id,
				statistics_widget.timeline,
				statistics_widget.statistic,
				statistics_widget.context,
				statistics_widget.priority
			FROM statistics_widget
			WHERE statistics_widget.context = :context
			ORDER BY statistics_widget.priority ASC
		""",
	)
	abstract fun getStatisticsWidgets(context: String): Flow<List<StatisticsWidgetEntity>>

	@Insert(entity = StatisticsWidgetEntity::class)
	abstract fun insertStatisticWidget(widget: StatisticsWidgetCreateEntity)

	@Update(entity = StatisticsWidgetEntity::class)
	abstract fun updateStatisticWidgetPriority(widget: StatisticsWidgetPriorityUpdateEntity)

	@Query("DELETE FROM statistics_widget WHERE id = :id")
	abstract fun deleteStatisticWidget(id: StatisticsWidgetID)
}

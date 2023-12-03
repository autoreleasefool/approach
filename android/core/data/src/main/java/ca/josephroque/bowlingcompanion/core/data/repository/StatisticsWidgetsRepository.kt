package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreateBowler
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreateLeague
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface StatisticsWidgetsRepository {
	fun getStatisticsWidgets(context: String): Flow<List<StatisticsWidget>>

	suspend fun insertStatisticWidget(widget: StatisticsWidgetCreateBowler)
	suspend fun insertStatisticWidget(widget: StatisticsWidgetCreateLeague)
	suspend fun deleteStatisticWidget(id: UUID)
	suspend fun updateStatisticsWidgetsOrder(widgets: List<UUID>)
}
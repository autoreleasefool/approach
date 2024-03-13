package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreate
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface StatisticsWidgetsRepository {
	fun getStatisticsWidgets(context: String): Flow<List<StatisticsWidget>>
	suspend fun getStatisticsWidgetChart(widget: StatisticsWidget): StatisticChartContent

	suspend fun insertStatisticWidget(widget: StatisticsWidgetCreate)
	suspend fun deleteStatisticWidget(id: UUID)
	suspend fun updateStatisticsWidgetsOrder(widgets: List<UUID>)
}

package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsWidgetDao
import ca.josephroque.bowlingcompanion.core.database.model.StatisticsWidgetPriorityUpdateEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.database.model.asModel
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineFirstStatisticsWidgetsRepository @Inject constructor(
	private val statisticsWidgetDao: StatisticsWidgetDao,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : StatisticsWidgetsRepository {
	override fun getStatisticsWidgets(context: String) =
		statisticsWidgetDao.getStatisticsWidgets(context).map { it.map { widget -> widget.asModel() } }

	override suspend fun insertStatisticWidget(widget: StatisticsWidgetCreate) = withContext(
		ioDispatcher,
	) {
		statisticsWidgetDao.insertStatisticWidget(widget.asEntity())
	}

	override suspend fun updateStatisticsWidgetsOrder(widgets: List<UUID>) = withContext(
		ioDispatcher,
	) {
		widgets.forEachIndexed { index, uuid ->
			statisticsWidgetDao.updateStatisticWidgetPriority(
				StatisticsWidgetPriorityUpdateEntity(uuid, index),
			)
		}
	}

	override suspend fun deleteStatisticWidget(id: UUID) = withContext(ioDispatcher) {
		statisticsWidgetDao.deleteStatisticWidget(id)
	}

	override suspend fun getStatisticsWidgetChart(widget: StatisticsWidget): StatisticChartContent =
		withContext(ioDispatcher) {
			StatisticChartContent.ChartUnavailable(id = widget.statistic)
		}
}

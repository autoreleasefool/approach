package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsWidgetDao
import ca.josephroque.bowlingcompanion.core.database.model.StatisticsWidgetPriorityUpdateEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.database.model.asModel
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreate
import ca.josephroque.bowlingcompanion.core.statistics.statisticInstanceFromID
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class OfflineFirstStatisticsWidgetsRepository @Inject constructor(
	private val statisticsRepository: StatisticsRepository,
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
			val statistic = statisticInstanceFromID(widget.statistic)
				?: return@withContext StatisticChartContent.ChartUnavailable(widget.statistic)

			val filter = widget.filter(Clock.System.now().toLocalDate())

			val chartContent = statisticsRepository.getStatisticsChart(
				statistic = statistic,
				filter = filter,
			)

			when (chartContent) {
				is StatisticChartContent.AveragingChart ->
					if (chartContent.data.isEmpty) {
						StatisticChartContent.DataMissing(widget.statistic)
					} else {
						chartContent
					}
				is StatisticChartContent.PercentageChart ->
					if (chartContent.data.isEmpty) {
						StatisticChartContent.DataMissing(widget.statistic)
					} else {
						chartContent
					}
				is StatisticChartContent.CountableChart ->
					if (chartContent.data.isEmpty) {
						StatisticChartContent.DataMissing(widget.statistic)
					} else {
						chartContent
					}
				is StatisticChartContent.ChartUnavailable ->
					StatisticChartContent.ChartUnavailable(widget.statistic)
				is StatisticChartContent.DataMissing ->
					StatisticChartContent.DataMissing(widget.statistic)
			}
		}
}

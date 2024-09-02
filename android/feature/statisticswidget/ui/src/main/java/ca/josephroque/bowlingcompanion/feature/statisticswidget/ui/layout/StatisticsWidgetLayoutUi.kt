package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetID
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

data class StatisticsWidgetLayoutUiState(
	val widgets: List<StatisticsWidget>,
	val widgetCharts: Map<StatisticsWidgetID, ChartContent>,
) {
	data class ChartContent(
		val chart: StatisticChartContent,
		val modelProducer: ChartEntryModelProducer,
	)
}

sealed interface StatisticsWidgetLayoutUiAction {
	data object ChangeLayoutClicked : StatisticsWidgetLayoutUiAction
	data class WidgetClicked(val widget: StatisticsWidget) : StatisticsWidgetLayoutUiAction
}

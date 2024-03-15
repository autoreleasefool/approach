package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID

data class StatisticsWidgetLayoutEditorUiState(
	val widgets: List<StatisticsWidget> = emptyList(),
	val widgetCharts: Map<UUID, ChartContent> = emptyMap(),
	val isDeleteModeEnabled: Boolean = false,
) {
	data class ChartContent(
		val chart: StatisticChartContent,
		val modelProducer: ChartEntryModelProducer,
	)
}

data class StatisticsWidgetLayoutEditorTopBarUiState(
	val isDeleteModeEnabled: Boolean = false,
)

sealed interface StatisticsWidgetLayoutEditorUiAction {
	data object BackClicked : StatisticsWidgetLayoutEditorUiAction
	data object AddWidgetClicked : StatisticsWidgetLayoutEditorUiAction

	data class WidgetClicked(val widget: StatisticsWidget) : StatisticsWidgetLayoutEditorUiAction
	data class WidgetMoved(val from: Int, val to: Int) : StatisticsWidgetLayoutEditorUiAction
	data class ToggleDeleteMode(val deleteMode: Boolean) : StatisticsWidgetLayoutEditorUiAction
}

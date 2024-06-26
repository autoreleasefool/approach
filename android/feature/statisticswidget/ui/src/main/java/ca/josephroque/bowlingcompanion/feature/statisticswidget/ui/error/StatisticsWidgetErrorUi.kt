package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID

data class StatisticsWidgetErrorUiState(
	val widget: StatisticsWidget = StatisticsWidget(
		source = StatisticsWidgetSource.Bowler(UUID.randomUUID()),
		id = UUID.randomUUID(),
		timeline = StatisticsWidgetTimeline.THREE_MONTHS,
		context = "",
		priority = 0,
		statistic = StatisticID.STRIKES,
	),
	val widgetChart: ChartEntryModelProducer =
		ChartEntryModelProducer(),
)

sealed interface StatisticsWidgetErrorTopBarUiAction {
	data object BackClicked : StatisticsWidgetErrorTopBarUiAction
}

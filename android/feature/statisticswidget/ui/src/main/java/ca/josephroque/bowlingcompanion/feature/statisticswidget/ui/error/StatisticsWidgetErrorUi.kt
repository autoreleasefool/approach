package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

data class StatisticsWidgetErrorUiState(
	val widget: StatisticsWidget = StatisticsWidget(
		source = StatisticsWidgetSource.Bowler(BowlerID.randomID()),
		id = StatisticsWidgetID.randomID(),
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

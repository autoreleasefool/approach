package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor

import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.GameAverageStatistic

data class StatisticsWidgetEditorUiState(
	val source: StatisticsWidgetSource? = null,
	val timeline: StatisticsWidgetTimeline = StatisticsWidgetTimeline.THREE_MONTHS,
	val statistic: Statistic = GameAverageStatistic(),
	val bowler: BowlerSummary? = null,
	val league: LeagueSummary? = null,
	val widget: StatisticsWidget? = null,
	val preview: StatisticChartContent? = null,
)

sealed interface StatisticsWidgetEditorUiAction {
	data object BackClicked : StatisticsWidgetEditorUiAction
	data object SaveClicked : StatisticsWidgetEditorUiAction

	data class TimelineSelected(val timeline: StatisticsWidgetTimeline) :
		StatisticsWidgetEditorUiAction

	data object StatisticClicked : StatisticsWidgetEditorUiAction
	data object BowlerClicked : StatisticsWidgetEditorUiAction
	data object LeagueClicked : StatisticsWidgetEditorUiAction
}

package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline

fun StatisticsWidgetTimeline.titleResourceId(): Int = when (this) {
	StatisticsWidgetTimeline.ALL_TIME -> R.string.statistics_widget_timeline_all_time
	StatisticsWidgetTimeline.ONE_YEAR -> R.string.statistics_widget_timeline_one_year
	StatisticsWidgetTimeline.ONE_MONTH -> R.string.statistics_widget_timeline_one_month
	StatisticsWidgetTimeline.SIX_MONTHS -> R.string.statistics_widget_timeline_six_months
	StatisticsWidgetTimeline.THREE_MONTHS -> R.string.statistics_widget_timeline_three_months
}

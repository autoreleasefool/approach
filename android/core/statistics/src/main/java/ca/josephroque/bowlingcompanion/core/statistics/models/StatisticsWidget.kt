package ca.josephroque.bowlingcompanion.core.statistics.models

import java.util.UUID

data class StatisticsWidget(
	val source: StatisticsWidgetSource,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: Int,
	val context: String,
	val priority: Int,
)

data class StatisticsWidgetCreate(
	val bowlerId: UUID,
	val leagueId: UUID?,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: Int,
	val context: String,
	val priority: Int,
)

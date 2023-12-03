package ca.josephroque.bowlingcompanion.core.statistics.models

import java.util.UUID

sealed interface StatisticsWidgetSource {
	data class Bowler(val bowlerId: UUID): StatisticsWidgetSource
	data class League(val leagueId: UUID): StatisticsWidgetSource
}

enum class StatisticsWidgetTimeline {
	ONE_MONTH,
	THREE_MONTHS,
	SIX_MONTHS,
	ONE_YEAR,
	ALL_TIME,
}
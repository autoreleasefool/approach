package ca.josephroque.bowlingcompanion.core.statistics.models

import java.util.UUID
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

sealed interface StatisticsWidgetSource {
	data class Bowler(val bowlerId: UUID) : StatisticsWidgetSource
	data class League(val bowlerId: UUID, val leagueId: UUID) : StatisticsWidgetSource
}

enum class StatisticsWidgetTimeline {
	ONE_MONTH,
	THREE_MONTHS,
	SIX_MONTHS,
	ONE_YEAR,
	ALL_TIME,
	;

	fun relativeTo(date: LocalDate): LocalDate? = when (this) {
		ONE_MONTH -> date.minus(1, DateTimeUnit.MONTH)
		THREE_MONTHS -> date.minus(3, DateTimeUnit.MONTH)
		SIX_MONTHS -> date.minus(6, DateTimeUnit.MONTH)
		ONE_YEAR -> date.minus(1, DateTimeUnit.YEAR)
		ALL_TIME -> null
	}
}

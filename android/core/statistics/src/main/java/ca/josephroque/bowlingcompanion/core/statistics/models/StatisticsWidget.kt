package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import java.util.UUID

data class StatisticsWidget(
	val source: StatisticsWidgetSource,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: StatisticID,
	val context: String,
	val priority: Int,
) {
	val filter: TrackableFilter
		get() = TrackableFilter(
			source = when (source) {
				is StatisticsWidgetSource.Bowler -> TrackableFilter.Source.Bowler(source.bowlerId)
				is StatisticsWidgetSource.League -> TrackableFilter.Source.League(source.leagueId)
			},
		)
}

data class StatisticsWidgetCreate(
	val bowlerId: UUID,
	val leagueId: UUID?,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: StatisticID,
	val context: String,
	val priority: Int,
)

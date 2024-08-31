package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import java.util.UUID
import kotlinx.datetime.LocalDate

data class StatisticsWidget(
	val source: StatisticsWidgetSource,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: StatisticID,
	val context: String,
	val priority: Int,
) {
	fun filter(currentDate: LocalDate): TrackableFilter = TrackableFilter(
		source = filterSource,
		series = TrackableFilter.SeriesFilter(
			startDate = timeline.relativeTo(currentDate),
		),
		aggregation = TrackableFilter.AggregationFilter.ACCUMULATE,
	)

	val filterSource: TrackableFilter.Source
		get() = when (source) {
			is StatisticsWidgetSource.Bowler -> TrackableFilter.Source.Bowler(source.bowlerId)
			is StatisticsWidgetSource.League -> TrackableFilter.Source.League(source.leagueId)
		}
}

data class StatisticsWidgetCreate(
	val bowlerId: BowlerID,
	val leagueId: UUID?,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: StatisticID,
	val context: String,
	val priority: Int,
)

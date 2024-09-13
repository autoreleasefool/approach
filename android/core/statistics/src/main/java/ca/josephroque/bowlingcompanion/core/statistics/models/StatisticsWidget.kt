package ca.josephroque.bowlingcompanion.core.statistics.models

import android.os.Parcelable
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import java.util.UUID
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class StatisticsWidgetID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): StatisticsWidgetID = StatisticsWidgetID(UUID.randomUUID())
		fun fromString(string: String): StatisticsWidgetID = StatisticsWidgetID(UUID.fromString(string))
	}
}

data class StatisticsWidget(
	val source: StatisticsWidgetSource,
	val id: StatisticsWidgetID,
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

	private val filterSource: TrackableFilter.Source
		get() = when (source) {
			is StatisticsWidgetSource.Bowler -> TrackableFilter.Source.Bowler(source.bowlerId)
			is StatisticsWidgetSource.League -> TrackableFilter.Source.League(source.leagueId)
		}
}

data class StatisticsWidgetCreate(
	val bowlerId: BowlerID,
	val leagueId: LeagueID?,
	val id: StatisticsWidgetID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: StatisticID,
	val context: String,
	val priority: Int,
)

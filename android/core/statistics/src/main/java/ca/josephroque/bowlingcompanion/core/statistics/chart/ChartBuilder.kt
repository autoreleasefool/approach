package ca.josephroque.bowlingcompanion.core.statistics.chart

import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import kotlinx.datetime.LocalDate

data class ChartBuilder(
	val aggregation: TrackableFilter.AggregationFilter,
) {
	fun buildChart(
		entries: Map<LocalDate, Statistic>,
		statistic: Statistic,
	): StatisticChartContent? {
		if (entries.values.all { it.isEmpty }) {
			return StatisticChartContent.DataMissing(statistic.titleResourceId)
		}

		return buildRelevantChartData(entries, 1L, statistic)
	}

	private fun buildRelevantChartData(
		entries: Map<LocalDate, Statistic>,
		timePeriod: Long,
		statistic: Statistic,
	): StatisticChartContent? {
		if (statistic is CountingStatistic) {
//			return StatisticChartContent.CountableChart(
//				chart = CountableChartData(
//					title = statistic.titleResourceId,
//					entries = entries.map { (date, stat) ->
//						CountableChartEntry(
//							id = date.toEpochMilliseconds(),
//							value = statistic.getValue(stat),
//							xAxis = CountableChartEntry.XAxisValue.Date(date, timePeriod),
//						)
//					},
//					isAccumulating = aggregation == TrackableFilter.AggregationFilter.ACCUMULATE,
//				),
//			)
		}

		return null
	}
}
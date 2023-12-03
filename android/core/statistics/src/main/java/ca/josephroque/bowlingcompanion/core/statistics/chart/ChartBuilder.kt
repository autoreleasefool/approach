package ca.josephroque.bowlingcompanion.core.statistics.chart

import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent

fun buildChartWithEntries(
	entries: Map<ChartEntryKey, Statistic>,
	statistic: Statistic,
	aggregation: TrackableFilter.AggregationFilter
): StatisticChartContent {
	if (entries.values.all { it.isEmpty }) {
		return StatisticChartContent.DataMissing(statistic.id)
	}

	if (statistic is CountingStatistic) {
		return StatisticChartContent.CountableChart(
			chart = CountableChartData(
				title = statistic.id.titleResourceId,
				entries = entries.map { (key, entryStatistic) ->
					CountableChartEntry(
						key = key,
						value = (entryStatistic as? CountingStatistic)?.count ?: 0,
					)
				},
				isAccumulating = false,
			),
		)
	}

	return StatisticChartContent.DataMissing(statistic.id)
}
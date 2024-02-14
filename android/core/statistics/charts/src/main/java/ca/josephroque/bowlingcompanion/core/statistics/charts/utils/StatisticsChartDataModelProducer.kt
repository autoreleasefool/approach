package ca.josephroque.bowlingcompanion.core.statistics.charts.utils

import ca.josephroque.bowlingcompanion.core.statistics.models.AveragingChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.PercentageChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.entryOf

fun StatisticChartContent.getModelEntries(): List<ChartEntry> = when (this) {
	is StatisticChartContent.CountableChart -> this.data.getModelEntries()
	is StatisticChartContent.AveragingChart -> this.data.getModelEntries()
	is StatisticChartContent.PercentageChart -> this.data.getModelEntries()
	else -> throw IllegalStateException("Unsupported chart type: $this")
}

fun CountableChartData.getModelEntries(): List<ChartEntry> = entries.map {
	entryOf(it.key.value, it.value.toFloat())
}
fun PercentageChartData.getModelEntries(): List<ChartEntry> = entries.map {
	entryOf(it.key.value, it.percentage)
}
fun AveragingChartData.getModelEntries(): List<ChartEntry> = entries.map {
	entryOf(it.key.value, it.value)
}

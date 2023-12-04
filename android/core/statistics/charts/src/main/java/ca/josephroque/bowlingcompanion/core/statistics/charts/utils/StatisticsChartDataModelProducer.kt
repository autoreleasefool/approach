package ca.josephroque.bowlingcompanion.core.statistics.charts.utils

import ca.josephroque.bowlingcompanion.core.statistics.models.AveragingChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.PercentageChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

fun StatisticChartContent.getModel(): ChartEntryModel {
		return when (this) {
			is StatisticChartContent.CountableChart -> this.data.getModel()
			is StatisticChartContent.AveragingChart -> this.data.getModel()
			is StatisticChartContent.PercentageChart -> this.data.getModel()
			else -> throw IllegalStateException("Unsupported chart type: $this")
	}
}

fun CountableChartData.getModel(): ChartEntryModel = ChartEntryModelProducer(
	entries.map { entryOf(it.key.value, it.value.toFloat()) }
).getModel()

fun PercentageChartData.getModel(): ChartEntryModel = ChartEntryModelProducer(
	entries.map { entryOf(it.key.value, it.percentage) }
).getModel()

fun AveragingChartData.getModel(): ChartEntryModel = ChartEntryModelProducer(
	entries.map { entryOf(it.key.value, it.value) }
).getModel()
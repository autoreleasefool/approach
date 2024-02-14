package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

data class AveragingChartData(
	val id: StatisticID,
	val entries: List<AveragingChartEntry>,
	val preferredTrendDirection: PreferredTrendDirection?,
) {
	val minimumValue: Double
	val maximumValue: Double
	val percentDifferenceOverFullTimeSpan: Double

	init {
		val minimumValue = entries.minOfOrNull { it.value } ?: 0.0
		val maximumValue = entries.maxOfOrNull { it.value } ?: 0.0
		val padding = (maximumValue - minimumValue) * 0.1
		this.minimumValue = minimumValue - padding
		this.maximumValue = maximumValue + padding

		val firstValue = entries.firstOrNull()?.value ?: 0.0
		val lastValue = entries.lastOrNull()?.value ?: 0.0
		percentDifferenceOverFullTimeSpan = if (firstValue != 0.0) {
			(lastValue - firstValue) / firstValue
		} else {
			0.0
		}
	}

	val isEmpty: Boolean
		get() = entries.size <= 1
}

data class AveragingChartEntry(
	val key: ChartEntryKey,
	val value: Double,
)

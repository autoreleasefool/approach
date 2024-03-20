package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

data class AveragingChartData(
	val id: StatisticID,
	val entries: List<AveragingChartEntry>,
	val preferredTrendDirection: PreferredTrendDirection?,
) {
	private val minimumValue: Double
	private val maximumValue: Double
	private val percentDifferenceOverFullTimeSpan: Double

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

	val numberOfVerticalTicks: Int
		get() {
			val maxValue = entries.maxOfOrNull { it.value } ?: 0.0
			return if (maxValue > 50) 8 else 4
		}

	val isEmpty: Boolean
		get() = entries.size <= 1

	val firstKey: ChartEntryKey?
		get() = entries.firstOrNull()?.key
}

data class AveragingChartEntry(
	val key: ChartEntryKey,
	val value: Double,
)

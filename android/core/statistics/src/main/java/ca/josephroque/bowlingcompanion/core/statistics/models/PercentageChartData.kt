package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import kotlin.math.abs

data class PercentageChartData(
	val id: StatisticID,
	val entries: List<PercentageChartEntry>,
	val isAccumulating: Boolean,
	val preferredTrendDirection: PreferredTrendDirection?,
) {
	private val percentDifferenceOverFullTimeSpan: Double?

	init {
		if (isAccumulating) {
			val firstValue = entries.firstOrNull()?.percentage ?: 0.0
			val lastValue = entries.lastOrNull()?.percentage ?: 0.0
			this.percentDifferenceOverFullTimeSpan = if (firstValue != 0.0) {
				(lastValue - firstValue) / abs(
					firstValue,
				)
			} else {
				0.0
			}
		} else {
			this.percentDifferenceOverFullTimeSpan = null
		}
	}

	val isEmpty: Boolean
		get() = entries.size <= 1

	val numberOfHorizontalTicks: Int
		get() = if (entries.size > 4) 4 else entries.size

	val firstKey: ChartEntryKey?
		get() = entries.firstOrNull()?.key
}

data class PercentageChartEntry(
	val key: ChartEntryKey,
	val numerator: Int,
	val denominator: Int,
	val percentage: Double,
)

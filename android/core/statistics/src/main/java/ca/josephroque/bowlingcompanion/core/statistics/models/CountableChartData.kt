package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

data class CountableChartData(
	val id: StatisticID,
	val entries: List<CountableChartEntry>,
	val isAccumulating: Boolean,
) {
	val isEmpty: Boolean
		get() = entries.isEmpty() || (isAccumulating && entries.size == 1)

	val numberOfVerticalTicks: Int
		get() {
			val maxValue = entries.maxOfOrNull { it.value } ?: 0
			return if (maxValue > 50) 8 else 4
		}

	val numberOfHorizontalTicks: Int
		get() = if (entries.size > 4) 4 else entries.size

	val firstKey: ChartEntryKey?
		get() = entries.firstOrNull()?.key
}

data class CountableChartEntry(
	val key: ChartEntryKey,
	val value: Int,
)

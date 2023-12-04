package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

data class CountableChartData(
	val id: StatisticID,
	val entries: List<CountableChartEntry>,
	val isAccumulating: Boolean,
) {
	val isEmpty: Boolean
		get() = entries.isEmpty() || (isAccumulating && entries.size == 1)
}

data class CountableChartEntry(
	val key: ChartEntryKey,
	val value: Int,
)
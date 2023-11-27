package ca.josephroque.bowlingcompanion.core.statistics.models

import kotlinx.datetime.LocalDate
import java.util.UUID

data class CountableChartData(
	val title: Int,
	val entries: List<CountableChartEntry>,
	val isAccumulating: Boolean,
) {
	val isEmpty: Boolean
		get() = entries.isEmpty() || (isAccumulating && entries.size == 1)
}

data class CountableChartEntry(
	val id: UUID,
	val value: Int,
	val xAxis: XAxisValue
) {
	sealed interface XAxisValue {
		data class Date(val date: LocalDate, val duration: Long): XAxisValue
		data class Game(val ordinal: Int): XAxisValue
	}
}
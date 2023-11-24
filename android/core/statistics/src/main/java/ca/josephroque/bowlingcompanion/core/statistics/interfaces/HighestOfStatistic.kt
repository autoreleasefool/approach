package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.Statistic

interface HighestOfStatistic: Statistic {
	var highest: Int

	override val supportsAggregation: Boolean
		get() = true

	override val supportsWidgets: Boolean
		get() = true

	override val formattedValue: String
		get() = highest.toString()

	override val isEmpty: Boolean
		get() = highest == 0

	fun aggregateWithStatistic(other: Statistic) {
		if (other is HighestOfStatistic) {
			highest = maxOf(highest, other.highest)
		}
	}
}
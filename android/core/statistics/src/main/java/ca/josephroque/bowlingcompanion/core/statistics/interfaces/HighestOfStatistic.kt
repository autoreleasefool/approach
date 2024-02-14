package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.Statistic

interface HighestOfStatistic : Statistic {
	var highest: Int

	override val supportsAggregation: Boolean
		get() = true

	override val supportsWidgets: Boolean
		get() = true

	override val formattedValue: String
		get() = highest.toString()

	override val formattedValueDescription: String?
		get() = null

	override val isEmpty: Boolean
		get() = highest == 0

	override fun aggregateWithStatistic(statistic: Statistic) {
		if (statistic is HighestOfStatistic) {
			highest = maxOf(highest, statistic.highest)
		}
	}
}

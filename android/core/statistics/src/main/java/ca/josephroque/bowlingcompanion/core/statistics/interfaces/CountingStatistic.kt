package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.Statistic

interface CountingStatistic : Statistic {
	var count: Int

	override val supportsAggregation: Boolean
		get() = true

	override val supportsWidgets: Boolean
		get() = true

	override val formattedValue: String
		get() = count.toString()

	override val formattedValueDescription: String?
		get() = null

	override val isEmpty: Boolean
		get() = count == 0

	override fun aggregateWithStatistic(statistic: Statistic) {
		if (statistic is CountingStatistic) {
			count += statistic.count
		}
	}
}

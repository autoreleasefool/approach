package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.Statistic

interface CountingStatistic: Statistic {
	var count: Int

	override val supportsAggregation: Boolean
		get() = true

	override val supportsWidgets: Boolean
		get() = true

	override val formattedValue: String
		get() = count.toString()

	override val isEmpty: Boolean
		get() = count == 0

	fun aggregateWithStatistic(other: Statistic) {
		if (other is CountingStatistic) {
			count += other.count
		}
	}
}
package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.Statistic

interface AveragingStatistic: Statistic {
	var total: Int
	var divisor: Int

	override val supportsAggregation: Boolean
		get() = false

	override val supportsWidgets: Boolean
		get() = true

	val average: Double
		get() = if (divisor == 0) 0.0 else total.toDouble() / divisor.toDouble()

	override val formattedValue: String
		get() = if (average == 0.0) "-" else String.format("%.1f", average)

	override val isEmpty: Boolean
		get() = divisor == 0

	override fun aggregateWithStatistic(statistic: Statistic) {
		if (statistic is AveragingStatistic) {
			total += statistic.total
			divisor += statistic.divisor
		}
	}
}
package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.utils.formatAsPercentage

interface PercentageStatistic: Statistic {
	val numeratorTitleResourceId: Int
	val denominatorTitleResourceId: Int
	val includeNumeratorInFormattedValue: Boolean

	var numerator: Int
	var denominator: Int

	override val supportsAggregation: Boolean
		get() = true

	override val supportsWidgets: Boolean
		get() = true

	val percentage: Double
		get() = if (denominator > 0) numerator.toDouble() / denominator.toDouble() else 0.0

	override val formattedValue: String
		get() = percentage.formatAsPercentage

	override val formattedValueDescription: String?
		get() = if (includeNumeratorInFormattedValue && numerator > 0) {
			String.format("%d/%d", numerator, denominator)
		} else {
			null
		}

	override val isEmpty: Boolean
		get() = denominator == 0

	override fun aggregateWithStatistic(statistic: Statistic) {
		if (statistic is PercentageStatistic) {
			numerator += statistic.numerator
			denominator += statistic.denominator
		}
	}
}
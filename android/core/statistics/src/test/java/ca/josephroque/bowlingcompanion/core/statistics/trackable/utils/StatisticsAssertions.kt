package ca.josephroque.bowlingcompanion.core.statistics.trackable.utils

import ca.josephroque.bowlingcompanion.core.statistics.interfaces.AveragingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.HighestOfStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.PercentageStatistic
import kotlin.test.assertEquals

fun <S : CountingStatistic> assertCounting(statistic: S, count: Int) {
	assertEquals(count, statistic.count)
	assertEquals(count.toString(), statistic.formattedValue)
	assertEquals(count == 0, statistic.isEmpty)
}

fun <S : HighestOfStatistic> assertHighestOf(statistic: S, highest: Int) {
	assertEquals(highest, statistic.highest)
	assertEquals(highest.toString(), statistic.formattedValue)
	assertEquals(highest == 0, statistic.isEmpty)
}

fun <S : AveragingStatistic> assertAveraging(statistic: S, total: Int, divisor: Int, formattedAs: String) {
	val average = if (divisor == 0) 0.0 else total.toDouble() / divisor.toDouble()
	assertEquals(total, statistic.total)
	assertEquals(divisor, statistic.divisor)
	assertEquals(divisor == 0, statistic.isEmpty)
	assertEquals(average, statistic.average)
	assertEquals(formattedAs, statistic.formattedValue)
}

fun <S : PercentageStatistic> assertPercentage(
	statistic: S,
	numerator: Int,
	denominator: Int,
	formattedAs: String,
	overridingIsEmptyExpectation: Boolean? = null,
) {
	val percentage = if (denominator > 0) numerator.toDouble() / denominator.toDouble() else 0.0
	assertEquals(numerator, statistic.numerator)
	assertEquals(denominator, statistic.denominator)
	assertEquals(overridingIsEmptyExpectation ?: (denominator == 0), statistic.isEmpty)
	assertEquals(statistic.percentage, percentage)
	assertEquals(statistic.formattedValue, formattedAs)
}

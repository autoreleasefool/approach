package ca.josephroque.bowlingcompanion.core.statistics.trackable.utils

import ca.josephroque.bowlingcompanion.core.statistics.interfaces.AveragingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.HighestOfStatistic
import kotlin.test.assertEquals

fun <S: CountingStatistic> assertCounting(
	statistic: S,
	count: Int,
) {
	assertEquals(count, statistic.count)
	assertEquals(count.toString(), statistic.formattedValue)
	assertEquals(count == 0, statistic.isEmpty)
}

fun <S: HighestOfStatistic> assertHighestOf(
	statistic: S,
	highest: Int,
) {
	assertEquals(highest, statistic.highest)
	assertEquals(highest.toString(), statistic.formattedValue)
	assertEquals(highest == 0, statistic.isEmpty)
}

fun <S: AveragingStatistic> assertAveraging(
	statistic: S,
	total: Int,
	divisor: Int,
	formattedAs: String,
) {
	val average = if (divisor == 0) 0.0 else total.toDouble() / divisor.toDouble()
	assertEquals(statistic.total, total)
	assertEquals(statistic.divisor, divisor)
	assertEquals(statistic.isEmpty, divisor == 0)
	assertEquals(average, statistic.average)
	assertEquals(formattedAs, statistic.formattedValue)
}
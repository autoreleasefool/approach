package ca.josephroque.bowlingcompanion.core.statistics.trackable.utils

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
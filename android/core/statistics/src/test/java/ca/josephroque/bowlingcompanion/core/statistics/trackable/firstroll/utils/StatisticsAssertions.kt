package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.utils

import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import kotlin.test.assertEquals

fun <S: CountingStatistic> assertCounting(
	statistic: S,
	count: Int,
) {
	assertEquals(count, statistic.count)
	assertEquals(count.toString(), statistic.formattedValue)
	assertEquals(count == 0, statistic.isEmpty)
}
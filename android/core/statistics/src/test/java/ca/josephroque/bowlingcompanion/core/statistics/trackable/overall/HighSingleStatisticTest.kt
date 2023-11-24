package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertHighestOf
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.game
import org.junit.Test

class HighSingleStatisticTest {
	@Test
	fun testAdjust_ByGame_Increments() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSingleStatistic(),
			byGames = listOf(
				game(index = 0, score = 123),
				game(index = 1, score = 234),
				game(index = 3, score = 99),
			),
		)

		assertHighestOf(statistic, 234)
	}

	@Test
	fun testAdjust_ByFrame_DoesNothing() {
		TODO()
	}

	@Test
	fun testAdjust_BySeries_DoesNothing() {
		TODO()
	}
}
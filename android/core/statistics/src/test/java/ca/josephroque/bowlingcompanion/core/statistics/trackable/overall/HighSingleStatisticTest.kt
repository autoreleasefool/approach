package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertHighestOf
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.game
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
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
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSingleStatistic(),
			bySeries = mockSeries(),
		)
		assertHighestOf(statistic, 0)
	}

	@Test
	fun testAdjustByFrame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSingleStatistic(),
			byFrames = mockFrames(),
		)
		assertHighestOf(statistic, 0)
	}
}
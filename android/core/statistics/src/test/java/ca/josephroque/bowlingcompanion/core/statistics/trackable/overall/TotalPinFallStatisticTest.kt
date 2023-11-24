package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.game
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import org.junit.Test

class TotalPinFallStatisticTest {
	@Test
	fun testAdjust_ByGame_Increments() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalPinFallStatistic(),
			byGames = listOf(
				game(index = 0, score = 123),
				game(index = 1, score = 234),
				game(index = 3, score = 99),
			),
		)

		assertCounting(statistic, 456)
	}

	@Test
	fun testAdjust_BySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalPinFallStatistic(),
			bySeries = mockSeries(),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjust_ByFrames_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalPinFallStatistic(),
			byFrames = mockFrames(),
		)

		assertCounting(statistic, 0)
	}
}
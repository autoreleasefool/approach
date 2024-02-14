package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.game
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import org.junit.Test

class NumberOfGamesStatisticTest {
	@Test
	fun testAdjust_ByGame() {
		val statistic = assertStatisticAdjusts(
			statistic = NumberOfGamesStatistic(),
			byGames = listOf(
				game(index = 0, score = 123),
				game(index = 1, score = 234),
				game(index = 3, score = 99),
			),
		)
		assertCounting(statistic, 3)
	}

	@Test
	fun testAdjust_ByGames_WithZeroScore_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = NumberOfGamesStatistic(),
			byGames = listOf(
				game(index = 0, score = 123),
				game(index = 1, score = 0),
				game(index = 3, score = 99),
			),
		)
		assertCounting(statistic, 2)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = NumberOfGamesStatistic(),
			bySeries = mockSeries(),
		)
		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByFrame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = NumberOfGamesStatistic(),
			byFrames = mockFrames(),
		)
		assertCounting(statistic, 0)
	}
}

package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertAveraging
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.game
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import org.junit.Test

class GameAverageStatisticTest {
	@Test
	fun testAdjust_ByGame() {
		val statistic = assertStatisticAdjusts(
			statistic = GameAverageStatistic(),
			byGames = listOf(
				game(index = 0, score = 123),
				game(index = 1, score = 234),
				game(index = 3, score = 99),
			),
		)
		assertAveraging(statistic, total = 456, divisor = 3, formattedAs = "152")
	}

	@Test
	fun testAdjust_ByGameWithZeroScore() {
		val statistic = assertStatisticAdjusts(
			statistic = GameAverageStatistic(),
			byGames = listOf(
				game(index = 0, score = 123),
				game(index = 1, score = 0),
				game(index = 3, score = 99),
			),
		)
		assertAveraging(statistic, total = 222, divisor = 2, formattedAs = "111")
	}

	@Test
	fun testAdjust_BySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = GameAverageStatistic(),
			bySeries = mockSeries(),
		)
		assertAveraging(statistic, total = 0, divisor = 0, formattedAs = "-")
	}

	@Test
	fun testAdjust_ByFrames_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = GameAverageStatistic(),
			byFrames = mockFrames(),
		)
		assertAveraging(statistic, total = 0, divisor = 0, formattedAs = "-")
	}
}
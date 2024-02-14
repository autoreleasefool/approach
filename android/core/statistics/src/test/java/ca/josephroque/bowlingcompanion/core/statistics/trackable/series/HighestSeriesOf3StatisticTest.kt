package ca.josephroque.bowlingcompanion.core.statistics.trackable.series

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertHighestOf
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.series
import org.junit.Test

class HighestSeriesOf3StatisticTest {
	@Test
	fun testAdjust_BySeriesWith3Games_IncrementsCount() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSeriesOf3Statistic(),
			bySeries = listOf(
				series(numberOfGames = 3, total = 123),
				series(numberOfGames = 3, total = 234),
				series(numberOfGames = 3, total = 99),
			),
		)
		assertHighestOf(statistic, 234)
	}

	@Test
	fun testAdjust_BySeriesWithout3Games_DoesNotIncrementCount() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSeriesOf3Statistic(),
			bySeries = listOf(
				series(numberOfGames = 0, total = 123),
				series(numberOfGames = 2, total = 234),
				series(numberOfGames = 4, total = 99),
			),
		)
		assertHighestOf(statistic, 0)
	}

	@Test
	fun testAdjustByFrames_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSeriesOf3Statistic(),
			byFrames = mockFrames(),
		)
		assertHighestOf(statistic, 0)
	}

	@Test
	fun testAdjustByGames_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = HighSeriesOf3Statistic(),
			byGames = mockGames(),
		)
		assertHighestOf(statistic, 0)
	}
}

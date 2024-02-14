package ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import org.junit.Test

class MatchesPlayedStatisticTest {
	@Test
	fun testAdjustByGame() {
		val statistic = assertStatisticAdjusts(
			statistic = MatchesPlayedStatistic(),
			byGames = mockGames(),
		)
		assertCounting(statistic, 9)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = MatchesPlayedStatistic(),
			bySeries = mockSeries(),
		)
		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByFrame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = MatchesPlayedStatistic(),
			byFrames = mockFrames(),
		)
		assertCounting(statistic, 0)
	}
}

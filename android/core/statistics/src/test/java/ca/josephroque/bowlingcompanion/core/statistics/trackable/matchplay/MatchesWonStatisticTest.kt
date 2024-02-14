package ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockFrames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import org.junit.Test

class MatchesWonStatisticTest {
	@Test
	fun testAdjustByGame() {
		val statistic = assertStatisticAdjusts(
			statistic = MatchesWonStatistic(),
			byGames = mockGames(),
		)
		assertPercentage(statistic, 3, 9, "33.3% (3)")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = MatchesWonStatistic(),
			bySeries = mockSeries(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByFrame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = MatchesWonStatistic(),
			byFrames = mockFrames(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}
}

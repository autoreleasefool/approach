package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class TotalRollsStatisticTest {
	@Test
	fun testAdjust_ByFrames_Increments() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalRollsStatistic(),
			byFrames = listOf(
				frame(0, listOf()),
				frame(
					1,
					listOf(
						roll(0, setOf()),
						roll(1, setOf(Pin.HEAD_PIN), didFoul = true),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN)),
						roll(2, setOf(Pin.RIGHT_THREE_PIN)),
					),
				),
			),
		)
		assertCounting(statistic, 5)
	}

	@Test
	fun testAdjust_BySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalRollsStatistic(),
			bySeries = mockSeries(),
		)
		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjust_ByGames_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalRollsStatistic(),
			byGames = mockGames(),
		)
		assertCounting(statistic, 0)
	}
}

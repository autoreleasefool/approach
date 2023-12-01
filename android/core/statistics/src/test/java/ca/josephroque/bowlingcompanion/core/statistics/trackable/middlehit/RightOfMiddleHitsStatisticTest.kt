package ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class RightOfMiddleHitsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithRightOfMiddleHit_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightOfMiddleHitsStatistic(),
			byFrames = listOf(
				frame(
					index = 0,
					rolls = listOf(
						roll(0, setOf(Pin.RIGHT_TWO_PIN))
					)
				),
				frame(
					index = 1,
					rolls = listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
						roll(1, setOf(Pin.RIGHT_TWO_PIN))
					)
				)
			)
		)

		assertPercentage(statistic, 1, 2, "50% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutRightOfMiddleHit_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = RightOfMiddleHitsStatistic(),
			byFrames = listOf(
				frame(
					index = 0,
					rolls = listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))
					)
				),
				frame(
					index = 1,
					rolls = listOf(
						roll(0, setOf(Pin.HEAD_PIN))
					)
				)
			)
		)

		assertPercentage(statistic, 0, 2, "0%")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightOfMiddleHitsStatistic(),
			bySeries = mockSeries()
		)
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightOfMiddleHitsStatistic(),
			byGames = mockGames()
		)
		assertPercentage(statistic, 0, 0, "0%")
	}
}

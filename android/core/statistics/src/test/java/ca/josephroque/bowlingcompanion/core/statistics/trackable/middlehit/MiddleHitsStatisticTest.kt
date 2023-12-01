package ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class MiddleHitsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithMiddleHit_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = MiddleHitsStatistic(),
			byFrames = listOf(
				frame(
					index = 0,
					rolls = listOf(
						roll(0, setOf(Pin.HEAD_PIN))
					)
				),
				frame(
					index = 1,
					rolls = listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.HEAD_PIN))
					)
				)
			)
		)

		assertPercentage(statistic, 1, 2, "50% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutMiddleHit_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = MiddleHitsStatistic(),
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
						roll(0, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN))
					)
				)
			)
		)

		assertPercentage(statistic, 0, 2, "0%")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = MiddleHitsStatistic(),
			bySeries = mockSeries()
		)
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = MiddleHitsStatistic(),
			byGames = mockGames()
		)
		assertPercentage(statistic, 0, 0, "0%")
	}
}

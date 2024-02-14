package ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class StrikeMiddleHitsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithStrikeMiddleHit_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikeMiddleHitsStatistic(),
			byFrames = listOf(
				frame(
					index = 0,
					rolls = listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
				frame(
					index = 1,
					rolls = listOf(
						roll(0, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					index = 2,
					rolls = listOf(
						roll(
							0,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
					),
				),
				frame(
					index = 2,
					rolls = listOf(
						roll(0, emptySet()),
						roll(
							1,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
					),
				),
			),
		)

		assertPercentage(statistic, 1, 2, "50% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutStrikeMiddleHit_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikeMiddleHitsStatistic(),
			byFrames = listOf(
				frame(
					index = 0,
					rolls = listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.HEAD_PIN)),
					),
				),
				frame(
					index = 1,
					rolls = listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
			),
		)

		assertPercentage(statistic, 0, 1, "0%")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikeMiddleHitsStatistic(),
			bySeries = mockSeries(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikeMiddleHitsStatistic(),
			byGames = mockGames(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}
}

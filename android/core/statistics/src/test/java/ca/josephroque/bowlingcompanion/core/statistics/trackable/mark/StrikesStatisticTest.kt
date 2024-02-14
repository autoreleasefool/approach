package ca.josephroque.bowlingcompanion.core.statistics.trackable.mark

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class StrikesStatisticTest {
	@Test
	fun testAdjust_ByFramesWithStrike_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikesStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					2,
					listOf(
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
					2,
					listOf(
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

		assertPercentage(statistic, 1, 4, "25% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutStrike_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikesStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.HEAD_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
			),
		)

		assertPercentage(statistic, 0, 2, "0%")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikesStatistic(),
			bySeries = mockSeries(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = StrikesStatistic(),
			byGames = mockGames(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}
}

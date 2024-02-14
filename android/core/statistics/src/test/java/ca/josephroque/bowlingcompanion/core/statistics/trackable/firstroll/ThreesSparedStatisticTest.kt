package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class ThreesSparedStatisticTest {
	@Test
	fun testAdjust_ByFramesWithThreesSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = ThreesSparedStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.RIGHT_THREE_PIN)),
						roll(1, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, setOf()),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
						roll(1, setOf()),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, setOf()),
					),
				),
			),
		)

		assertPercentage(statistic, 2, 2, "100% (2)")
	}

	@Test
	fun testAdjust_ByFramesWithoutThreesSpared_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = ThreesSparedStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf()),
						roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf()),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.RIGHT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
			),
		)

		assertPercentage(statistic, 0, 2, "0%", overridingIsEmptyExpectation = true)
	}

	@Test
	fun testAdjust_InLastFrame_ByFramesWithThreesSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = ThreesSparedStatistic(),
			byFrames = listOf(
				// Open attempt
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(2, setOf(Pin.LEFT_TWO_PIN)),
					),
				),
				// Spared attempt, followed by strike
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(
							2,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
					),
				),
				// Spared attempt, followed by open
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(2, setOf()),
					),
				),
				// Strike, followed by spared attempt
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(
							0,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
						roll(1, setOf(Pin.RIGHT_THREE_PIN)),
						roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				// Strike followed by open attempt
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(
							0,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
						roll(1, setOf(Pin.RIGHT_THREE_PIN)),
						roll(2, setOf()),
					),
				),
				// Two strikes, followed by spareable shot
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(
							0,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
						roll(
							1,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
						roll(2, setOf(Pin.LEFT_THREE_PIN)),
					),
				),
				// Three strikes
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(
							0,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
						roll(
							1,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
						roll(
							2,
							setOf(
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.HEAD_PIN,
								Pin.RIGHT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
							),
						),
					),
				),
			),
		)

		assertPercentage(statistic, 3, 5, "60% (3)")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = ThreesSparedStatistic(),
			bySeries = mockSeries(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = ThreesSparedStatistic(),
			byGames = mockGames(),
		)
		assertPercentage(statistic, 0, 0, "0%")
	}
}

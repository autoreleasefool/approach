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

class LeftThreesSparedStatisticTest {
	@Test
	fun testAdjust_ByFramesWithLeftThreesSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftThreesSparedStatistic(),
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
						roll(1, emptySet()),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
			),
		)

		assertPercentage(statistic, numerator = 1, denominator = 1, formattedAs = "100% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutLeftThreesSpared_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftThreesSparedStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, emptySet()),
						roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, emptySet()),
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

		assertPercentage(
			statistic,
			numerator = 0,
			denominator = 1,
			formattedAs = "0%",
			overridingIsEmptyExpectation = true,
		)
	}

	@Test
	fun testAdjust_InLastFrame_ByFramesWithLeftThreesSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftThreesSparedStatistic(),
			byFrames = listOf(
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
						roll(2, setOf(Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
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
				frame(
					Game.NUMBER_OF_FRAMES - 1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
						roll(2, emptySet()),
					),
				),
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
						roll(1, setOf(Pin.LEFT_THREE_PIN)),
						roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
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
						roll(1, setOf(Pin.LEFT_THREE_PIN)),
						roll(2, emptySet()),
					),
				),
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

		assertPercentage(statistic, numerator = 3, denominator = 5, formattedAs = "60% (3)")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftThreesSparedStatistic(),
			bySeries = mockSeries(),
		)

		assertPercentage(statistic, numerator = 0, denominator = 0, formattedAs = "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftThreesSparedStatistic(),
			byGames = mockGames(),
		)

		assertPercentage(statistic, numerator = 0, denominator = 0, formattedAs = "0%")
	}
}

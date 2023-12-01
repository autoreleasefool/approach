package ca.josephroque.bowlingcompanion.core.statistics.trackable.pinsleft

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class TotalPinsLeftOnDeckStatisticTest {
	@Test
	fun testAdjustByFrames() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalPinsLeftOnDeckStatistic(),
			byFrames = listOf(
				frame(
					index = 0,
					rolls = listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
						roll(2, setOf(Pin.HEAD_PIN))
					)
				),
				frame(
					index = 1,
					rolls = listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
						roll(0, emptySet())
					)
				),
				frame(
					index = 9,
					rolls = listOf(
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						)
					)
				),
				frame(
					index = 9,
					rolls = listOf(
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(
							0,
							setOf(
								Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						)
					)
				),
				frame(
					index = 9,
					rolls = listOf(
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(0, emptySet())
					)
				),
				frame(
					index = 9,
					rolls = listOf(
						roll(0, emptySet()),
						roll(
							0,
							setOf(
								Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN
							)
						),
						roll(0, setOf(Pin.LEFT_TWO_PIN))
					)
				)
			)
		)

		assertCounting(statistic, 43)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalPinsLeftOnDeckStatistic(),
			bySeries = mockSeries()
		)
		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = TotalPinsLeftOnDeckStatistic(),
			byGames = mockGames()
		)
		assertCounting(statistic, 0)
	}
}

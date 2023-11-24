package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class ChopOffsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithChopOffs_IncrementsCount() {
		val statistic = assertStatisticAdjusts(
			statistic = ChopOffsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(0, setOf()),
				)),
			),
		)

		assertCounting(statistic, 4)
	}

	@Test
	fun testAdjust_ByFramesWithoutChopOffs_DoesNotIncrementCount() {
		val statistic = assertStatisticAdjusts(
			statistic = ChopOffsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
					roll(1, setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
			),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		TODO()
	}

	@Test
	fun testAdjustByGames_DoesNothing() {
		TODO()
	}
}
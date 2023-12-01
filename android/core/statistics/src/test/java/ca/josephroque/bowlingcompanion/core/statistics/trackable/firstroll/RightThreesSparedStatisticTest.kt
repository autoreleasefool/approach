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

class RightThreesSparedTest {
	@Test
	fun testAdjust_ByFramesWithRightThreesSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightThreesSparedStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf()),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf()),
				)),
			),
		)

		assertPercentage(statistic, 1, 1, "100% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutRightThreesSpared_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = RightThreesSparedStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf()),
					roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf()),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
			),
		)

		assertPercentage(statistic, 0, 1, "0%", overridingIsEmptyExpectation = true)
	}

	@Test
	fun testAdjust_InLastFrame_ByFramesWithRightThreesSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightThreesSparedStatistic(),
			byFrames = listOf(
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN)),
				)),
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf()),
				)),
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.RIGHT_THREE_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.RIGHT_THREE_PIN)),
					roll(2, setOf()),
				)),
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.RIGHT_THREE_PIN)),
				)),
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
			)
		)

		assertPercentage(statistic, 3, 5, "60% (3)")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightThreesSparedStatistic(),
			bySeries = mockSeries(),
		)

		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightThreesSparedStatistic(),
			byGames = mockGames(),
		)

		assertPercentage(statistic, 0, 0, "0%")
	}
}

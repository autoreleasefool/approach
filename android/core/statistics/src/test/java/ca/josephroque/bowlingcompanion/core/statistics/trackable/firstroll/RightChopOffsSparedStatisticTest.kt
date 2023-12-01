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

class RightChopOffsSparedStatisticTest {
	@Test
	fun testAdjust_ByFramesWithRightChopOffsSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightChopOffsSparedStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, emptySet()),
				)),
			),
		)

		assertPercentage(statistic, numerator = 1, denominator = 2, formattedAs = "50% (1)")
	}

	@Test
	fun testAdjust_ByFramesWithoutRightChopOffsSpared_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = RightChopOffsSparedStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
					roll(1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, emptySet()),
				)),
			),
		)

		assertPercentage(statistic, numerator = 0, denominator = 1, formattedAs = "0%", overridingIsEmptyExpectation = true)
	}

	@Test
	fun testAdjust_InLastFrame_ByFramesWithRightChopsSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightChopOffsSparedStatistic(),
			byFrames = listOf(
				// Open attempt
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_THREE_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN)),
				)),
				// Spared attempt, followed by strike
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				// Spared attempt, followed by open
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
					roll(2, emptySet()),
				)),
				// Strike, followed by spared attempt
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				)),
				// Strike followed by open attempt
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, emptySet()),
				)),
				// Two strikes, followed by spareable shot
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				// Three strikes
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
			)
		)

		assertPercentage(statistic, numerator = 3, denominator = 5, formattedAs = "60% (3)")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightChopOffsSparedStatistic(),
			bySeries = mockSeries(),
		)

		assertPercentage(statistic, numerator = 0, denominator = 0, formattedAs = "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightChopOffsSparedStatistic(),
			byGames = mockGames(),
		)

		assertPercentage(statistic, numerator = 0, denominator = 0, formattedAs = "0%")
	}
}

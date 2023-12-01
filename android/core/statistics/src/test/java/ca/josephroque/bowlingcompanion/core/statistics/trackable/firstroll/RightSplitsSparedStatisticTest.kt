package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class RightSplitsSparedStatisticTest {
	@Test
	fun testAdjust_ByFramesWithRightSplitsSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightSplitsSparedStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, emptySet()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
				frame(5, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
			),
		)

		assertPercentage(statistic, numerator = 2, denominator = 3, formattedAs = "66.7% (2)")
	}

	@Test
	fun testAdjust_ByFramesWithoutRightSplitsSpared_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = RightSplitsSparedStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
					roll(2, setOf(Pin.RIGHT_TWO_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, emptySet()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.RIGHT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
				frame(5, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
			),
		)

		assertPercentage(statistic, numerator = 0, denominator = 2, formattedAs = "0%", overridingIsEmptyExpectation = true)
	}

	@Test
	fun testAdjust_InLastFrame_ByFramesWithRightSplitsSpared_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightSplitsSparedStatistic(),
			byFrames = listOf(
				// Open attempt
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN)),
				)),
				// Spared attempt, followed by strike
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				// Spared attempt, followed by open
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, emptySet()),
				)),
				// Strike, followed by spared attempt
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				// Strike followed by open attempt
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(2, emptySet()),
				)),
				// Two strikes, followed by spareable shot
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				// Three strikes
				frame(Game.NumberOfFrames - 1, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(2, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(countHeadPin2AsHeadPin = false, countSplitWithBonusAsSplit = true),
		)

		assertPercentage(statistic, numerator = 3, denominator = 5, formattedAs = "60% (3)")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightSplitsSparedStatistic(),
			bySeries = mockSeries(),
		)

		assertPercentage(statistic, numerator = 0, denominator = 0, formattedAs = "0%")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightSplitsSparedStatistic(),
			byGames = mockGames(),
		)

		assertPercentage(statistic, numerator = 0, denominator = 0, formattedAs = "0%")
	}
}

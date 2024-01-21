package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class LeftSplitsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithLeftSplits_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftSplitsStatistic(),
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
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
			),
		)

		assertCounting(statistic, 3)
	}

	@Test
	fun testAdjust_ByFramesWithoutLeftSplits_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftSplitsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)),
				)),
			),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjust_ByFramesWithLeftSplitsWithBonus_WithBonusEnabled_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftSplitsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, emptySet()),
				)),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(countHeadPin2AsHeadPin = false, countSplitWithBonusAsSplit = true),
		)

		assertCounting(statistic, 3)
	}

	@Test
	fun testAdjust_ByFramesWithLeftSplitsWithBonus_WithBonusDisabled_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftSplitsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, emptySet()),
				)),
				frame(4, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(countHeadPin2AsHeadPin = false, countSplitWithBonusAsSplit = false),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftSplitsStatistic(),
			bySeries = mockSeries(),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = LeftSplitsStatistic(),
			byGames = mockGames(),
		)

		assertCounting(statistic, 0)
	}
}

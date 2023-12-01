package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class RightTapsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithRight_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = RightTapsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.HEAD_PIN)),
				)),
			),
		)

		assertCounting(statistic, 1)
	}

	@Test
	fun testAdjust_ByFramesWithoutRights_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = RightTapsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN)),
					roll(1, setOf(Pin.HEAD_PIN)),
				)),
			),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightTapsStatistic(),
			bySeries = mockSeries(),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = RightTapsStatistic(),
			byGames = mockGames(),
		)

		assertCounting(statistic, 0)
	}
}

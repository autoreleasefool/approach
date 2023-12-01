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

class HeadPinsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithHeadPins_IncrementsCount() {
		val statistic = assertStatisticAdjusts(
			statistic = HeadPinsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.HEAD_PIN)),
				)),
			),
		)

		assertCounting(statistic, 1)
	}

	@Test
	fun testAdjust_ByFramesWithHeadPin2_WithHeadPin2Enabled_IncrementsCount() {
		val statistic = assertStatisticAdjusts(
			statistic = HeadPinsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.LEFT_TWO_PIN)),
				)),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(countHeadPin2AsHeadPin = true, countSplitWithBonusAsSplit = false),
		)

		assertCounting(statistic, 3)
	}

	@Test
	fun testAdjust_ByFramesWithHeadPin2_WithHeadPin2Disabled_DoesNotIncrement() {
		val statistic = assertStatisticAdjusts(
			statistic = HeadPinsStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)),
				)),
				frame(2, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
				)),
				frame(3, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.LEFT_TWO_PIN)),
				)),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(countHeadPin2AsHeadPin = false, countSplitWithBonusAsSplit = false),
		)

		assertCounting(statistic, 1)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = AcesStatistic(),
			bySeries = mockSeries(),
		)
		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByGames_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = AcesStatistic(),
			byGames = mockGames(),
		)
		assertCounting(statistic, 0)
	}
}
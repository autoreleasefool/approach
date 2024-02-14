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

class SplitsStatisticTest {
	@Test
	fun testAdjust_ByFramesWithSplits_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = SplitsStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
			),
		)

		assertCounting(statistic, 4)
	}

	@Test
	fun testAdjust_ByFramesWithoutSplits_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = SplitsStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)),
					),
				),
			),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjust_ByFramesWithSplitsWithBonus_WithBonusEnabled_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = SplitsStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(
				countHeadPin2AsHeadPin = false,
				countSplitWithBonusAsSplit = true,
			),
		)

		assertCounting(statistic, 4)
	}

	@Test
	fun testAdjust_ByFramesWithSplitsWithBonus_WithBonusDisabled_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = SplitsStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					),
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					2,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
					),
				),
				frame(
					3,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, emptySet()),
					),
				),
				frame(
					4,
					listOf(
						roll(0, setOf(Pin.HEAD_PIN)),
					),
				),
			),
			perFrameConfiguration = TrackablePerFrameConfiguration(
				countHeadPin2AsHeadPin = false,
				countSplitWithBonusAsSplit = false,
			),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = SplitsStatistic(),
			bySeries = mockSeries(),
		)

		assertCounting(statistic, 0)
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = SplitsStatistic(),
			byGames = mockGames(),
		)

		assertCounting(statistic, 0)
	}
}

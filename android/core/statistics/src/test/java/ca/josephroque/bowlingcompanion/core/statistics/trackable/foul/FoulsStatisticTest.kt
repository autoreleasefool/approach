package ca.josephroque.bowlingcompanion.core.statistics.trackable.foul

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertPercentage
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import org.junit.Test

class FoulsStatisticTests {
	@Test
	fun testAdjust_ByFramesWithFouls_Adjusts() {
		val statistic = assertStatisticAdjusts(
			statistic = FoulsStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, emptySet(), didFoul = true)
					)
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN), didFoul = true),
						roll(1, setOf(Pin.HEAD_PIN), didFoul = true),
					)
				),
			),
		)

		assertPercentage(statistic, numerator = 3, denominator = 3, formattedAs = "100%")
	}

	@Test
	fun testAdjust_ByFramesWithoutFouls_DoesNotAdjust() {
		val statistic = assertStatisticAdjusts(
			statistic = FoulsStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, emptySet(), didFoul = false),
					)
				),
				frame(
					1,
					listOf(
						roll(0, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN), didFoul = false),
						roll(1, setOf(Pin.HEAD_PIN), didFoul = false),
					)
				),
			),
		)

		assertPercentage(statistic, 0, 3, "0%")
	}

	@Test
	fun testAdjustBySeriesDoesNothing() {
		val statistic = assertStatisticAdjusts(statistic = FoulsStatistic(), bySeries = mockSeries())
		assertPercentage(statistic, 0, 0, "0%")
	}

	@Test
	fun testAdjustByGameDoesNothing() {
		val statistic = assertStatisticAdjusts(statistic = FoulsStatistic(), byGames = mockGames())
		assertPercentage(statistic, 0, 0, "0%")
	}
}

package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertAveraging
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockGames
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.mockSeries
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import ca.josephroque.bowlingcompanion.core.testing.id
import org.junit.Test

class AverageFirstRollStatisticTest {
	@Test
	fun testAdjustByFrames() {
		val statistic = assertStatisticAdjusts(
			statistic = AverageFirstRollStatistic(),
			byFrames = listOf(
				frame(
					0,
					listOf(
						roll(0, setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_TWO_PIN)),
						roll(1, setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN)),
						roll(2, emptySet()),
					),
					gameId = id(0),
				),
				frame(
					1,
					listOf(
						roll(
							0,
							setOf(
								Pin.HEAD_PIN,
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
						roll(
							1,
							setOf(
								Pin.HEAD_PIN,
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
					),
					gameId = id(0),
				),
				frame(
					1,
					listOf(
						roll(0, emptySet()),
						roll(
							1,
							setOf(
								Pin.HEAD_PIN,
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
					),
					gameId = id(0),
				),
				frame(
					9,
					listOf(
						roll(
							0,
							setOf(
								Pin.HEAD_PIN,
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
						roll(
							1,
							setOf(
								Pin.HEAD_PIN,
								Pin.LEFT_TWO_PIN,
								Pin.LEFT_THREE_PIN,
								Pin.RIGHT_TWO_PIN,
								Pin.RIGHT_THREE_PIN,
							),
						),
						roll(
							2,
							setOf(
								Pin.RIGHT_TWO_PIN,
							),
						),
					),
					gameId = id(0),
				),
			),
		)

		assertAveraging(statistic, 51, 6, "8.5")
	}

	@Test
	fun testAdjustBySeries_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = AverageFirstRollStatistic(),
			bySeries = mockSeries(),
		)
		assertAveraging(statistic, 0, 0, "-")
	}

	@Test
	fun testAdjustByGame_DoesNothing() {
		val statistic = assertStatisticAdjusts(
			statistic = AverageFirstRollStatistic(),
			byGames = mockGames(),
		)
		assertAveraging(statistic, 0, 0, "-")
	}
}

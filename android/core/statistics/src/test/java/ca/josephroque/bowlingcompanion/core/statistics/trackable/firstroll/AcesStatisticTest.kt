package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.utils.assertCounting
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.utils.assertStatisticAdjusts
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.utils.roll
import org.junit.Test

class AcesStatisticTest {
	@Test
	fun testAdjust_ByFramesWithAces_IncrementsCount() {
		val statistic = assertStatisticAdjusts(
			statistic = AcesStatistic(),
			byFrames = listOf(
				frame(0, listOf(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)),
				)),
				frame(1, listOf(
					roll(0, setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)),
					roll(1, setOf(Pin.HEAD_PIN)),
				)),
			),
		)

		assertCounting(statistic, 1)
	}
}
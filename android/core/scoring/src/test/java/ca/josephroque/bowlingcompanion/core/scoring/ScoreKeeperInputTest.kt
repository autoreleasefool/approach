package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import kotlin.test.assertEquals
import org.junit.Test

class ScoreKeeperInputTest {
	@Test
	fun whenFramesWithRolls_Parsed_ReturnsValidInput() {
		val frames = listOf(
			ScoreableFrame(
				index = 0,
				rolls = listOf(
					ScoreableFrame.Roll(index = 0, pinsDowned = Pin.fullDeck(), didFoul = false),
					ScoreableFrame.Roll(index = 1, pinsDowned = setOf(), didFoul = false),
					ScoreableFrame.Roll(index = 2, pinsDowned = setOf(), didFoul = false),
				),
			),
			ScoreableFrame(
				index = 1,
				rolls = listOf(
					ScoreableFrame.Roll(index = 0, pinsDowned = setOf(Pin.HEAD_PIN), didFoul = true),
					ScoreableFrame.Roll(index = 1, pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), didFoul = false),
					ScoreableFrame.Roll(index = 2, pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN), didFoul = false),
				),
			),
		)

		val input = ScoreKeeperInput.fromFrames(frames)

		assertEquals(
			input,
			ScoreKeeperInput(
				rolls = listOf(
					listOf(
						ScoreKeeperInput.Roll(
							frameIndex = 0,
							rollIndex = 0,
							pinsDowned = Pin.fullDeck(),
							didFoul = false,
						),
						ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, pinsDowned = setOf(), didFoul = false),
						ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 2, pinsDowned = setOf(), didFoul = false),
					),
					listOf(
						ScoreKeeperInput.Roll(
							frameIndex = 1,
							rollIndex = 0,
							pinsDowned = setOf(Pin.HEAD_PIN),
							didFoul = true,
						),
						ScoreKeeperInput.Roll(
							frameIndex = 1,
							rollIndex = 1,
							pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
							didFoul = false,
						),
						ScoreKeeperInput.Roll(
							frameIndex = 1,
							rollIndex = 2,
							pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN),
							didFoul = false,
						),
					),
				),
			),
		)
	}
}

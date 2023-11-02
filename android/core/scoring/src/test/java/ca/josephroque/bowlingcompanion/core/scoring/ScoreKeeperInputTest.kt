package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import org.junit.Test
import kotlin.test.assertEquals

class ScoreKeeperInputTest {
	@Test
	fun testFromBitString_whenBitStringWithFoul_ReturnsRollWithFoul() {
		val bitString = "100000"
		val roll = ScoreKeeperInput.Roll.fromBitString(frameIndex = 0, rollIndex = 1, bitString = bitString)
		assertEquals(
			ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, pinsDowned = setOf(), didFoul = true),
			roll,
		)
	}

	@Test
	fun testFromBitString_whenBitStringWithPinsDowned_ReturnsRollWithPinsDowned() {
		val bitString = "010011"
		val roll = ScoreKeeperInput.Roll.fromBitString(frameIndex = 0, rollIndex = 1, bitString = bitString)
		assertEquals(
			ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN), didFoul = false),
			roll,
		)
	}

	@Test
	fun testFromBitString_whenBitStringIsNull_ReturnsEmptyRoll() {
		val roll = ScoreKeeperInput.Roll.fromBitString(frameIndex = 0, rollIndex = 1, bitString = null)
		assertEquals(
			ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, pinsDowned = setOf(), didFoul = false),
			roll,
		)
	}

	@Test
	fun whenFramesWithRolls_Parsed_ReturnsValidInput() {
		val frames = listOf(
			ScoreableFrame(index = 0, roll0 = "011111", roll1 = null, roll2 = null),
			ScoreableFrame(index = 1, roll0 = "100100", roll1 = "011000", roll2 = "000011"),
		)

		val input = ScoreKeeperInput.fromFrames(frames)

		assertEquals(
			input,
			ScoreKeeperInput(
				rolls = listOf(
					listOf(
						ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 0, pinsDowned = Pin.fullDeck(), didFoul = false),
						ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, pinsDowned = setOf(), didFoul = false),
						ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 2, pinsDowned = setOf(), didFoul = false),
					),
					listOf(
						ScoreKeeperInput.Roll(frameIndex = 1, rollIndex = 0, pinsDowned = setOf(Pin.HEAD_PIN), didFoul = true),
						ScoreKeeperInput.Roll(frameIndex = 1, rollIndex = 1, pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), didFoul = false),
						ScoreKeeperInput.Roll(frameIndex = 1, rollIndex = 2, pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN), didFoul = false),
					),
				)
			)
		)
	}
}
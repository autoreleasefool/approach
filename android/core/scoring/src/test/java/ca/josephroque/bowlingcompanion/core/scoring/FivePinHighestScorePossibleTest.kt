package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class FivePinHighestPossiblePossibleTest {
	@Test
	fun testWithOneStrike_AndAtStartOfFrame2_Returns450() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, Pin.fullDeck(), false),
				),
			)
		)

		assertHighestScorePossibleEquals(450, input)
	}

	@Test
	fun testWithOneStrike_AndAtStartOfFrame2_WithInvalidRolls_Returns450() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, Pin.fullDeck(), false),
					ScoreKeeperInput.Roll(0, 1, emptySet(), false),
					ScoreKeeperInput.Roll(0, 2, emptySet(), false),
				),
			)
		)

		assertHighestScorePossibleEquals(450, input)
	}

	@Test
	fun testWithOneSpare_AndAtStartOfFrame2_Returns435() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, emptySet(), false),
					ScoreKeeperInput.Roll(0, 1, Pin.fullDeck(), false),
				),
			)
		)

		assertHighestScorePossibleEquals(435, input)
	}

	@Test
	fun testWithOneSpare_AndAtStartOfFrame2_WithInvalidRolls_Returns435() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, emptySet(), false),
					ScoreKeeperInput.Roll(0, 1, Pin.fullDeck(), false),
					ScoreKeeperInput.Roll(0, 2, emptySet(), false),
				),
			)
		)

		assertHighestScorePossibleEquals(435, input)
	}

	@Test
	fun testWithTwoStrikes_AndAtStartOfFrame3_Returns450() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, Pin.fullDeck(), false),
				),
				listOf(
					ScoreKeeperInput.Roll(1, 0, Pin.fullDeck(), false),
				),
			)
		)

		assertHighestScorePossibleEquals(450, input)
	}

	@Test
	fun testOpenFrame_AndAtStartOfFrame2_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, emptySet(), false),
					ScoreKeeperInput.Roll(0, 1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), false),
					ScoreKeeperInput.Roll(0, 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN), false),
				),
			)
		)

		assertHighestScorePossibleEquals(415, input)
	}

	@Test
	fun testIncompleteFrame_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(0, 0, emptySet(), false),
					ScoreKeeperInput.Roll(0, 1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), false),
				),
			)
		)

		assertHighestScorePossibleEquals(420, input)
	}

	@Test
	fun testStrikeFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
			)
		)

		assertHighestScorePossibleEquals(450, input)
	}

	@Test
	fun testStrikeFrame_ThenSpareFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				spareFrame(1),
			)
		)

		assertHighestScorePossibleEquals(420, input)
	}

	@Test
	fun testStrikeFrame_ThenFifteenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				fifteenFrame(1),
			)
		)

		assertHighestScorePossibleEquals(400, input)
	}

	@Test
	fun testStrikeFrame_ThenOpenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				openFrame(1),
			)
		)

		assertHighestScorePossibleEquals(390, input)
	}

	@Test
	fun testStrikeFrame_ThenTwoRollsWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				twoRollsWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(400, input)
	}

	@Test
	fun testStrikeFrame_ThenTwoRollsWithMisses_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				twoRollsWithMisses(1),
			)
		)

		assertHighestScorePossibleEquals(390, input)
	}

	@Test
	fun testStrikeFrame_ThenOneRollWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				oneRollWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(420, input)
	}

	@Test
	fun testStrikeFrame_ThenOneRollWithMiss_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				oneRollWithMiss(1),
			)
		)

		assertHighestScorePossibleEquals(420, input)
	}

	@Test
	fun testStrikeFrame_ThenZeroRolls_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				strikeFrame(0),
				zeroRolls(),
			)
		)

		assertHighestScorePossibleEquals(450, input)
	}

	@Test
	fun testSpareFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
			)
		)

		assertHighestScorePossibleEquals(435, input)
	}

	@Test
	fun testSpareFrame_ThenFifteenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				fifteenFrame(1),
			)
		)

		assertHighestScorePossibleEquals(395, input)
	}

	@Test
	fun testSpareFrame_ThenOpenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				openFrame(1),
			)
		)

		assertHighestScorePossibleEquals(385, input)
	}

	@Test
	fun testSpareFrame_ThenTwoRollsWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				twoRollsWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(395, input)
	}

	@Test
	fun testSpareFrame_ThenTwoRollsWithMisses_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				twoRollsWithMisses(1),
			)
		)

		assertHighestScorePossibleEquals(390, input)
	}

	@Test
	fun testSpareFrame_ThenOneRollWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				oneRollWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(410, input)
	}

	@Test
	fun testSpareFrame_ThenOneRollWithMiss_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				oneRollWithMiss(1),
			)
		)

		assertHighestScorePossibleEquals(405, input)
	}

	@Test
	fun testSpareFrame_ThenZeroRolls_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				spareFrame(0),
				zeroRolls(),
			)
		)

		assertHighestScorePossibleEquals(435, input)
	}

	@Test
	fun testFifteenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
			)
		)

		assertHighestScorePossibleEquals(420, input)
	}

	@Test
	fun testFifteenFrame_ThenSpareFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				spareFrame(1),
			)
		)

		assertHighestScorePossibleEquals(405, input)
	}

	@Test
	fun testFifteenFrame_ThenOpenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				openFrame(1),
			)
		)

		assertHighestScorePossibleEquals(385, input)
	}

	@Test
	fun testFifteenFrame_ThenTwoRollsWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				twoRollsWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(390, input)
	}

	@Test
	fun testFifteenFrame_ThenTwoRollsWithMisses_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				twoRollsWithMisses(1),
			)
		)

		assertHighestScorePossibleEquals(390, input)
	}

	@Test
	fun testFifteenFrame_ThenOneRollWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				oneRollWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(405, input)
	}

	@Test
	fun testFifteenFrame_ThenOneRollWithMiss_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				oneRollWithMiss(1),
			)
		)

		assertHighestScorePossibleEquals(405, input)
	}

	@Test
	fun testFifteenFrame_ThenZeroRolls_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				fifteenFrame(0),
				zeroRolls(),
			)
		)

		assertHighestScorePossibleEquals(420, input)
	}

	@Test
	fun testOpenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
			)
		)

		assertHighestScorePossibleEquals(415, input)
	}

	@Test
	fun testOpenFrame_ThenSpareFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				spareFrame(1),
			)
		)

		assertHighestScorePossibleEquals(400, input)
	}

	@Test
	fun testOpenFrame_ThenFifteenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				fifteenFrame(1),
			)
		)

		assertHighestScorePossibleEquals(385, input)
	}

	@Test
	fun testOpenFrame_ThenOpenFrame_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				openFrame(1),
			)
		)

		assertHighestScorePossibleEquals(380, input)
	}

	@Test
	fun testOpenFrame_ThenTwoRollsWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				twoRollsWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(385, input)
	}

	@Test
	fun testOpenFrame_ThenTwoRollsWithMisses_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				twoRollsWithMisses(1),
			)
		)

		assertHighestScorePossibleEquals(385, input)
	}

	@Test
	fun testOpenFrame_ThenOneRollWithPins_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				oneRollWithPins(1),
			)
		)

		assertHighestScorePossibleEquals(400, input)
	}

	@Test
	fun testOpenFrame_ThenOneRollWithMiss_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				oneRollWithMiss(1),
			)
		)

		assertHighestScorePossibleEquals(400, input)
	}

	@Test
	fun testOpenFrame_ThenZeroRolls_ReturnsCorrectScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				openFrame(0),
				zeroRolls(),
			)
		)

		assertHighestScorePossibleEquals(415, input)
	}

	private fun strikeFrame(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, Pin.fullDeck(), didFoul),
	)

	private fun spareFrame(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, emptySet(), didFoul),
		ScoreKeeperInput.Roll(index, 1, Pin.fullDeck(), false),
	)

	private fun fifteenFrame(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), didFoul),
		ScoreKeeperInput.Roll(index, 1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN), false),
		ScoreKeeperInput.Roll(index, 2, setOf(Pin.HEAD_PIN), false),
	)

	private fun openFrame(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, emptySet(), didFoul),
		ScoreKeeperInput.Roll(index, 1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), false),
		ScoreKeeperInput.Roll(index, 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN), false),
	)

	private fun twoRollsWithPins(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), didFoul),
		ScoreKeeperInput.Roll(index, 1, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN), false),
	)

	private fun twoRollsWithMisses(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, emptySet(), didFoul),
		ScoreKeeperInput.Roll(index, 1, emptySet(), false),
	)

	private fun oneRollWithPins(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN), didFoul),
	)

	private fun oneRollWithMiss(index: Int, didFoul: Boolean = false): List<ScoreKeeperInput.Roll> = listOf(
		ScoreKeeperInput.Roll(index, 0, emptySet(), didFoul),
	)

	private fun zeroRolls(): List<ScoreKeeperInput.Roll> = emptyList()

	private suspend fun assertHighestScorePossibleEquals(expected: Int, input: ScoreKeeperInput) {
		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val highestPossible = scoreKeeper.calculateHighestScorePossible(input)

		assertEquals(expected, highestPossible)
	}
}
package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import kotlin.test.assertEquals
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FivePinCalculateScoreTest {
	@Test
	fun testCalculateScore_ForBasicGame_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 6, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 6, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 7, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 7, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
				),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = true),
					),
					score = 25,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
					),
					score = 40,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 70,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "13", isSecondaryValue = true),
					),
					score = 113,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "13", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = true),
					),
					score = 143,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = true),
					),
					score = 163,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 153,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HP", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 158,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 203,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "HP", isSecondaryValue = false),
					),
					score = 238,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_WithSkippedRolls_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
				),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "13", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = true),
					),
					score = 28,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 41,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HP", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = 46,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_WithInvalidRollsAfterStrikes_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
					ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(
							Pin.LEFT_THREE_PIN,
							Pin.LEFT_TWO_PIN,
							Pin.RIGHT_TWO_PIN,
							Pin.RIGHT_THREE_PIN,
						),
					),
					ScoreKeeperInput.Roll(frameIndex = 1, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 1, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "10", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = true),
					),
					score = 25,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "10", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 35,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_WithEmptyFrames_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = true),
					),
					score = 15,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = 30,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_WithFouls_ReturnsScore() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 0, didFoul = true, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 1, didFoul = true, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 0, rollIndex = 2, didFoul = true, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
				listOf(),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = true, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = true, display = "-", isSecondaryValue = false),
					),
					score = 0,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 0,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = 30,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = null, isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = null, isSecondaryValue = false),
					),
					score = null,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_ExampleGame1() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 1, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 5, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 5, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 6, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 6, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 9, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HP", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
					),
					score = 30,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 60,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "13", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = true),
					),
					score = 90,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "13", isSecondaryValue = true),
					),
					score = 118,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 131,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "A", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 142,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = true),
					),
					score = 162,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = true),
					),
					score = 182,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 192,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_ExampleGame2() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 5, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 5, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 7, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 7, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
				),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "H2", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "3", isSecondaryValue = false),
					),
					score = 15,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HS", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
					),
					score = 30,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 75,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
					),
					score = 115,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "10", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = true),
					),
					score = 140,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 150,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "12", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "12", isSecondaryValue = true),
					),
					score = 177,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "12", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 189,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 234,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "HP", isSecondaryValue = false),
					),
					score = 269,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_ExampleGame3() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = Pin.fullDeck(),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 2, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 2, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 4, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(frameIndex = 4, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 6, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 7, rollIndex = 2, didFoul = false, pinsDowned = setOf()),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
				),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "15", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "13", isSecondaryValue = true),
					),
					score = 28,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "13", isSecondaryValue = true),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = true),
					),
					score = 41,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 54,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "13", isSecondaryValue = true),
					),
					score = 82,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 95,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = true),
					),
					score = 115,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 125,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "-", isSecondaryValue = false),
					),
					score = 135,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
					),
					score = 150,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
					),
					score = 165,
				),
			),
			score,
		)
	}

	@Test
	fun testCalculateScore_ExampleGame4() = runTest {
		val input = ScoreKeeperInput(
			rolls = listOf(
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 0,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 1,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 2,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 0,
						didFoul = true,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 3,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 4, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(
						frameIndex = 4,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 5,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(frameIndex = 6, rollIndex = 1, didFoul = false, pinsDowned = setOf()),
					ScoreKeeperInput.Roll(
						frameIndex = 6,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_THREE_PIN, Pin.HEAD_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 7,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 8,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
				listOf(
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 0,
						didFoul = false,
						pinsDowned = Pin.fullDeck(),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 1,
						didFoul = false,
						pinsDowned = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN),
					),
					ScoreKeeperInput.Roll(
						frameIndex = 9,
						rollIndex = 2,
						didFoul = false,
						pinsDowned = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN),
					),
				),
			),
		)

		val scoreKeeper = FivePinScoreKeeper(UnconfinedTestDispatcher())
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "A", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "2", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
					),
					score = 0,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "HS", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
					),
					score = 0,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "12", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "12", isSecondaryValue = true),
					),
					score = 12,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "12", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
					),
					score = 22,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
					),
					score = 37,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
					),
					score = 62,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
					),
					score = 77,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HS", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "11", isSecondaryValue = true),
					),
					score = 103,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "A", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
					),
					score = 133,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
						ScoringRoll(index = 1, didFoul = false, display = "HS", isSecondaryValue = false),
						ScoringRoll(index = 2, didFoul = false, display = "/", isSecondaryValue = false),
					),
					score = 163,
				),
			),
			score,
		)
	}
}

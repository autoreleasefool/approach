package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FivePinScoreKeeperTest {
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 25,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 40,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 70,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "13"),
					),
					score = 113,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "13"),
						ScoringRoll(index = 2, didFoul = false, display = "2"),
					),
					score = 143,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 163,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 153,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HP"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 158,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 203,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "X"),
						ScoringRoll(index = 2, didFoul = false, display = "HP"),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "13"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 28,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 41,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HP"),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = 46,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "10"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 25,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "10"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 35,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "-"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "-"),
						ScoringRoll(index = 1, didFoul = true, display = "-"),
						ScoringRoll(index = 2, didFoul = true, display = "-"),
					),
					score = 0,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 0,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = 30,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
					),
					score = null,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = null),
						ScoringRoll(index = 1, didFoul = false, display = null),
						ScoringRoll(index = 2, didFoul = false, display = null),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HP"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "2"),
					),
					score = 30,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 60,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "13"),
						ScoringRoll(index = 2, didFoul = false, display = "2"),
					),
					score = 90,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "13"),
					),
					score = 118,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 131,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "A"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 142,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 162,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 182,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "H2"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "3"),
					),
					score = 15,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HS"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "2"),
					),
					score = 30,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 75,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "10"),
					),
					score = 115,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "10"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 140,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 150,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "12"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "12"),
					),
					score = 177,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "12"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 189,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 234,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "X"),
						ScoringRoll(index = 2, didFoul = false, display = "HP"),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "15"),
						ScoringRoll(index = 2, didFoul = false, display = "13"),
					),
					score = 28,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "13"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 41,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 54,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "L"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "13"),
					),
					score = 82,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 95,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "R"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 115,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 125,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "-"),
					),
					score = 135,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 150,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "5"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
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

		val scoreKeeper = FivePinScoreKeeper()
		val score = scoreKeeper.calculateScore(input)
		assertEquals(
			listOf(
				ScoringFrame(
					index = 0,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "A"),
						ScoringRoll(index = 1, didFoul = false, display = "2"),
						ScoringRoll(index = 2, didFoul = false, display = "2"),
					),
					score = 0,
				),
				ScoringFrame(
					index = 1,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "HS"),
						ScoringRoll(index = 1, didFoul = false, display = "5"),
						ScoringRoll(index = 2, didFoul = false, display = "2"),
					),
					score = 0,
				),
				ScoringFrame(
					index = 2,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "12"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "12"),
					),
					score = 12,
				),
				ScoringFrame(
					index = 3,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = true, display = "12"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "10"),
					),
					score = 22,
				),
				ScoringFrame(
					index = 4,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 37,
				),
				ScoringFrame(
					index = 5,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "10"),
					),
					score = 62,
				),
				ScoringFrame(
					index = 6,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "C/O"),
						ScoringRoll(index = 1, didFoul = false, display = "-"),
						ScoringRoll(index = 2, didFoul = false, display = "5"),
					),
					score = 77,
				),
				ScoringFrame(
					index = 7,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "HS"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "11"),
					),
					score = 103,
				),
				ScoringFrame(
					index = 8,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "A"),
						ScoringRoll(index = 1, didFoul = false, display = "/"),
						ScoringRoll(index = 2, didFoul = false, display = "15"),
					),
					score = 133,
				),
				ScoringFrame(
					index = 9,
					rolls = listOf(
						ScoringRoll(index = 0, didFoul = false, display = "X"),
						ScoringRoll(index = 1, didFoul = false, display = "HS"),
						ScoringRoll(index = 2, didFoul = false, display = "/"),
					),
					score = 163,
				),
			),
			score,
		)
	}
}

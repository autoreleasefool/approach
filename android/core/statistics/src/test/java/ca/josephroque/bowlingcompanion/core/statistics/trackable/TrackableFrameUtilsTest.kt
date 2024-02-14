package ca.josephroque.bowlingcompanion.core.statistics.trackable

import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.frame
import ca.josephroque.bowlingcompanion.core.statistics.trackable.utils.roll
import ca.josephroque.bowlingcompanion.core.statistics.utils.RollPair
import ca.josephroque.bowlingcompanion.core.statistics.utils.firstRolls
import ca.josephroque.bowlingcompanion.core.statistics.utils.rollPairs
import ca.josephroque.bowlingcompanion.core.statistics.utils.secondRolls
import kotlin.test.assertEquals
import org.junit.Test

class TrackableFrameUtilsTest {
	@Test
	fun testFirstRolls_InAnyFrame_ReturnsFirstRoll() {
		val frame1 = frame(
			0,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(0, setOf(Pin.HEAD_PIN))), frame1.firstRolls)

		val frame2 = frame(
			1,
			listOf(
				roll(
					0,
					setOf(
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
					),
				),
			),
		)

		assertEquals(
			listOf(
				roll(
					0,
					setOf(
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
					),
				),
			),
			frame2.firstRolls,
		)

		val frame3 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(0, setOf(Pin.HEAD_PIN))), frame3.firstRolls)
	}

	fun testFirstRolls_WithNoRolls_ReturnsEmptyArray() {
		val frame = frame(0, emptyList())
		assertEquals(emptyList(), frame.firstRolls)
	}

	fun testFirstRolls_InLastFrame_WithNoStrikesOrSpares_ReturnsFirstRoll() {
		val frame = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, emptySet()),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(0, emptySet())), frame.firstRolls)
	}

	@Test
	fun testFirstRolls_InLastFrame_WithOneStrike_ReturnsTwoRolls() {
		val frame = frame(
			index = Game.NUMBER_OF_FRAMES - 1,
			rolls = listOf(
				roll(
					index = 0,
					setOf(
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
					),
				),
				roll(index = 1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(index = 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				roll(
					index = 0,
					setOf(
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.HEAD_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.RIGHT_TWO_PIN,
					),
				),
				roll(index = 1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
			),
			frame.firstRolls,
		)
	}

	@Test
	fun testFirstRolls_InLastFrame_WithOneSpare_ReturnsTwoRolls() {
		val frame = frame(
			index = Game.NUMBER_OF_FRAMES - 1,
			rolls = listOf(
				roll(index = 0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				roll(index = 1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(index = 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				roll(index = 0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
				roll(index = 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
			frame.firstRolls,
		)
	}

	@Test
	fun testFirstRolls_InLastFrame_WithTwoStrikes_ReturnsThreeRolls() {
		val frame = frame(
			index = Game.NUMBER_OF_FRAMES - 1,
			rolls = listOf(
				roll(
					index = 0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(
					index = 1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(index = 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				roll(
					index = 0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(
					index = 1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(index = 2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
			frame.firstRolls,
		)
	}

	@Test
	fun testFirstRolls_InLastFrame_WithThreeStrikes_ReturnsThreeRolls() {
		val frame = frame(
			index = Game.NUMBER_OF_FRAMES - 1,
			rolls = listOf(
				roll(
					index = 0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(
					index = 1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(
					index = 2,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
			),
		)

		assertEquals(
			listOf(
				roll(
					index = 0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(
					index = 1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
				roll(
					index = 2,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
					),
				),
			),
			frame.firstRolls,
		)
	}

	@Test
	fun testSecondRolls_InAnyFrame_ReturnsSecondRoll() {
		val frame1 = frame(
			0,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))), frame1.secondRolls)

		val frame2 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))), frame2.secondRolls)
	}

	@Test
	fun testSecondRolls_WithNoRolls_ReturnsEmptyArray() {
		val frame1 = frame(0, emptyList())
		assertEquals(emptyList(), frame1.secondRolls)
	}

	@Test
	fun testSecondRolls_WithNoSecondRolls_ReturnsEmptyArray() {
		val frame1 = frame(0, listOf(roll(0, setOf(Pin.HEAD_PIN))))
		assertEquals(emptyList(), frame1.secondRolls)
	}

	@Test
	fun testSecondRolls_InLastFrame_WithNoStrikesOrSpares_ReturnsSecondRoll() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, emptySet()),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))), frame1.secondRolls)
	}

	@Test
	fun testSecondRolls_InLastFrame_WithOneStrike_ReturnsSecondRoll() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
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
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN))), frame1.secondRolls)
	}

	@Test
	fun testSecondRolls_InLastFrame_WithOneSpare_ReturnsOneRoll() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(listOf(roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))), frame1.secondRolls)
	}

	@Test
	fun testSecondRolls_InLastFrame_WithTwoStrikes_ReturnsNoRolls() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(
					0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(
					1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(emptyList(), frame1.secondRolls)
	}

	@Test
	fun testSecondRolls_InLastFrame_WithThreeStrikes_ReturnsNoRolls() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(
					0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(
					1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(
					2,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
			),
		)

		assertEquals(emptyList(), frame1.secondRolls)
	}

	@Test
	fun testRollPairs_InAnyFrame_ReturnsPair() {
		val frame1 = frame(
			0,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				RollPair(roll(0, setOf(Pin.HEAD_PIN)), roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))),
			),
			frame1.rollPairs,
		)

		val frame2 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				RollPair(roll(0, setOf(Pin.HEAD_PIN)), roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))),
			),
			frame2.rollPairs,
		)
	}

	@Test
	fun testRollPairs_WithNoRolls_ReturnsEmptyArray() {
		val frame1 = frame(0, emptyList())
		assertEquals(emptyList(), frame1.rollPairs)
	}

	@Test
	fun testRollPairs_WithNoSecondRolls_ReturnsEmptyArray() {
		val frame1 = frame(0, listOf(roll(0, setOf(Pin.HEAD_PIN))))
		assertEquals(emptyList(), frame1.rollPairs)
	}

	@Test
	fun testRollPairs_InLastFrame_WithNoStrikesOrSpares_ReturnsPair() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, emptySet()),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				RollPair(roll(0, emptySet()), roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN))),
			),
			frame1.rollPairs,
		)
	}

	@Test
	fun testRollPairs_InLastFrame_WithOneStrike_ReturnsPair() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
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
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				RollPair(
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
					roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				),
			),
			frame1.rollPairs,
		)
	}

	@Test
	fun testRollPairs_InLastFrame_WithOneSpare_ReturnsPair() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
				roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(
			listOf(
				RollPair(
					roll(0, setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)),
					roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)),
				),
			),
			frame1.rollPairs,
		)
	}

	@Test
	fun testRollPairs_InLastFrame_WithTwoStrikes_ReturnsNoPair() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(
					0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(
					1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(2, setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)),
			),
		)

		assertEquals(emptyList(), frame1.rollPairs)
	}

	@Test
	fun testRollPairs_InLastFrame_WithThreeStrikes_ReturnsNoPair() {
		val frame1 = frame(
			Game.NUMBER_OF_FRAMES - 1,
			listOf(
				roll(
					0,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(
					1,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
				roll(
					2,
					setOf(
						Pin.HEAD_PIN,
						Pin.RIGHT_TWO_PIN,
						Pin.RIGHT_THREE_PIN,
						Pin.LEFT_TWO_PIN,
						Pin.LEFT_THREE_PIN,
					),
				),
			),
		)

		assertEquals(emptyList(), frame1.rollPairs)
	}
}

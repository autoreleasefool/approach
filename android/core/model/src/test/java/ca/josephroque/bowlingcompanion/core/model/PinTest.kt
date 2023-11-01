package ca.josephroque.bowlingcompanion.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PinTest {
	@Test
	fun testPinCount() {
		assertEquals(Pin.HEAD_PIN.pinCount, 5)
		assertEquals(Pin.LEFT_THREE_PIN.pinCount, 3)
		assertEquals(Pin.RIGHT_THREE_PIN.pinCount, 3)
		assertEquals(Pin.LEFT_TWO_PIN.pinCount, 2)
		assertEquals(Pin.RIGHT_TWO_PIN.pinCount, 2)
	}

	@Test
	fun testIsHeadPin() {
		val deck1 = setOf(Pin.HEAD_PIN)
		assertTrue(deck1.isHeadPin())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck2.isHeadPin())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isHeadPin())
	}

	@Test
	fun testIsHeadPin2() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isHeadPin2())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck2.isHeadPin2())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isHeadPin2())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isHeadPin2())
	}

	@Test
	fun testIsLeftTap() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isLeftTap())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck2.isLeftTap())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isLeftTap())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isLeftTap())
	}

	@Test
	fun testIsRightTap() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isRightTap())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck2.isRightTap())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isRightTap())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isRightTap())
	}

	@Test
	fun testIsTap() {
		val deck1: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isTap())
		val deck2: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck2.isTap())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isTap())

		val emptyDeck: Set<Pin> = setOf()
		assertFalse(emptyDeck.isTap())
	}

	@Test
	fun testIsAce() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isAce())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck2.isAce())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isAce())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isAce())
	}

	@Test
	fun testIsLeftChopOff() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isLeftChop())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck2.isLeftChop())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isLeftChop())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isLeftChop())
	}

	@Test
	fun testIsRightChopOff() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isRightChop())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck2.isRightChop())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isRightChop())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isRightChop())
	}

	@Test
	fun testIsChopOff() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isChop())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck2.isChop())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isChop())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isChop())
	}

	@Test
	fun testIsLeftSplit() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertTrue(deck1.isLeftSplit())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck2.isLeftSplit())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck3.isLeftSplit())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck4.isLeftSplit())

		val deck5 = setOf(Pin.HEAD_PIN)
		assertFalse(deck5.isLeftSplit())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isLeftSplit())
	}

	@Test
	fun testIsLeftSplitWithBonus() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isLeftSplitWithBonus())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck2.isLeftSplitWithBonus())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck3.isLeftSplitWithBonus())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck4.isLeftSplitWithBonus())

		val deck5 = setOf(Pin.HEAD_PIN)
		assertFalse(deck5.isLeftSplitWithBonus())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isLeftSplitWithBonus())
	}

	@Test
	fun testIsRightSplit() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isRightSplit())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck2.isRightSplit())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck3.isRightSplit())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck4.isRightSplit())

		val deck5 = setOf(Pin.HEAD_PIN)
		assertFalse(deck5.isRightSplit())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isRightSplit())
	}

	@Test
	fun testIsRightSplitWithBonus() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isRightSplitWithBonus())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck2.isRightSplitWithBonus())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck3.isRightSplitWithBonus())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck4.isRightSplitWithBonus())

		val deck5 = setOf(Pin.HEAD_PIN)
		assertFalse(deck5.isRightSplitWithBonus())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isRightSplitWithBonus())
	}

	@Test
	fun testIsSplit() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isSplit())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertTrue(deck2.isSplit())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck3.isSplit())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck4.isSplit())

		val deck5 = setOf(Pin.HEAD_PIN)
		assertFalse(deck5.isSplit())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isSplit())
	}

	@Test
	fun testIsSplitWithBonus() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isSplitWithBonus())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck2.isSplitWithBonus())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck3.isSplitWithBonus())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck4.isSplitWithBonus())

		val deck5 = setOf(Pin.HEAD_PIN)
		assertFalse(deck5.isSplitWithBonus())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isSplitWithBonus())
	}

	@Test
	fun testIsHitLeftOfMiddle() {
		val deck1 = setOf(Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isHitLeftOfMiddle())

		val deck2 = setOf(Pin.LEFT_THREE_PIN)
		assertTrue(deck2.isHitLeftOfMiddle())

		val deck3 = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertTrue(deck3.isHitLeftOfMiddle())

		val deck4 = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck4.isHitLeftOfMiddle())

		val deck5 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck5.isHitLeftOfMiddle())

		val deck6 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck6.isHitLeftOfMiddle())

		val deck7 = setOf(Pin.HEAD_PIN)
		assertFalse(deck7.isHitLeftOfMiddle())

		val deck8 = setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck8.isHitLeftOfMiddle())

		val deck9 = setOf(Pin.RIGHT_THREE_PIN)
		assertFalse(deck9.isHitLeftOfMiddle())

		val deck10 = setOf(Pin.RIGHT_TWO_PIN)
		assertFalse(deck10.isHitLeftOfMiddle())

		val deck11 = setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck11.isHitLeftOfMiddle())

		val deck12 = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck12.isHitLeftOfMiddle())

		val deck13 = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck13.isHitLeftOfMiddle())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isHitLeftOfMiddle())
	}

	@Test
	fun testIsHitRightOfMiddle() {
		val deck1 = setOf(Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isHitRightOfMiddle())

		val deck2 = setOf(Pin.RIGHT_THREE_PIN)
		assertTrue(deck2.isHitRightOfMiddle())

		val deck3 = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck3.isHitRightOfMiddle())

		val deck4 = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck4.isHitRightOfMiddle())

		val deck5 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck5.isHitRightOfMiddle())

		val deck6 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck6.isHitRightOfMiddle())

		val deck7 = setOf(Pin.HEAD_PIN)
		assertFalse(deck7.isHitRightOfMiddle())

		val deck8 = setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck8.isHitRightOfMiddle())

		val deck9 = setOf(Pin.LEFT_THREE_PIN)
		assertFalse(deck9.isHitRightOfMiddle())

		val deck10 = setOf(Pin.LEFT_TWO_PIN)
		assertFalse(deck10.isHitRightOfMiddle())

		val deck11 = setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck11.isHitRightOfMiddle())

		val deck12 = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck12.isHitRightOfMiddle())

		val deck13 = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck13.isHitRightOfMiddle())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isHitRightOfMiddle())
	}

	@Test
	fun testIsMiddleHit() {
		val deck1 = setOf(Pin.HEAD_PIN)
		assertTrue(deck1.isMiddleHit())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck2.isMiddleHit())

		val deck3 = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck3.isMiddleHit())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck4.isMiddleHit())

		val deck5 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertTrue(deck5.isMiddleHit())

		val deck6 = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck6.isMiddleHit())

		val deck7 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck7.isMiddleHit())

		val deck8 = setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck8.isMiddleHit())

		val deck9 = setOf(Pin.LEFT_THREE_PIN)
		assertFalse(deck9.isMiddleHit())

		val deck10 = setOf(Pin.LEFT_TWO_PIN)
		assertFalse(deck10.isMiddleHit())

		val deck11 = setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck11.isMiddleHit())

		val deck12 = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck12.isMiddleHit())

		val deck13 = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck13.isMiddleHit())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isMiddleHit())
	}

	@Test
	fun testIsLeftTwelve() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isLeftTwelve())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck2.isLeftTwelve())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isLeftTwelve())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isLeftTwelve())
	}

	@Test
	fun testIsRightTwelve() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.isRightTwelve())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck2.isRightTwelve())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isRightTwelve())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isRightTwelve())
	}

	@Test
	fun testIsTwelve() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertTrue(deck1.isTwelve())

		val deck2 = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck2.isTwelve())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isTwelve())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.isTwelve())
	}

	@Test
	fun testIsLeftFive() {
		val deck1: Set<Pin> = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertTrue(deck1.isLeftFive())

		val deck2: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)
		assertFalse(deck2.isLeftFive())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isLeftFive())

		val deck4: Set<Pin> = setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck4.isLeftFive())

		val deck5: Set<Pin> = setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck5.isLeftFive())

		val emptyDeck: Set<Pin> = emptySet()
		assertFalse(emptyDeck.isLeftFive())
	}

	@Test
	fun testisRightFive() {
		val deck1: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isRightFive())

		val deck2: Set<Pin> = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertFalse(deck2.isRightFive())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isRightFive())

		val deck4: Set<Pin> = setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck4.isRightFive())

		val deck5: Set<Pin> = setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck5.isRightFive())

		val emptyDeck: Set<Pin> = emptySet()
		assertFalse(emptyDeck.isRightFive())
	}

	@Test
	fun testisFive() {
		val deck1: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isFive())

		val deck2: Set<Pin> = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertTrue(deck2.isFive())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isFive())

		val deck4: Set<Pin> = setOf(Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertFalse(deck4.isFive())

		val deck5: Set<Pin> = setOf(Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertFalse(deck5.isFive())

		val emptyDeck: Set<Pin> = emptySet()
		assertFalse(emptyDeck.isFive())
	}

	@Test
	fun testisLeftThree() {
		val deck1: Set<Pin> = setOf(Pin.LEFT_THREE_PIN)
		assertTrue(deck1.isLeftThree())

		val deck2: Set<Pin> = setOf(Pin.RIGHT_THREE_PIN)
		assertFalse(deck2.isLeftThree())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isLeftThree())

		val deck4: Set<Pin> = setOf(Pin.LEFT_TWO_PIN)
		assertFalse(deck4.isLeftThree())

		val deck5: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN)
		assertFalse(deck5.isLeftThree())

		val emptyDeck: Set<Pin> = emptySet()
		assertFalse(emptyDeck.isLeftThree())
	}

	@Test
	fun testisRightThree() {
		val deck1: Set<Pin> = setOf(Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isRightThree())

		val deck2: Set<Pin> = setOf(Pin.LEFT_THREE_PIN)
		assertFalse(deck2.isRightThree())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isRightThree())

		val deck4: Set<Pin> = setOf(Pin.LEFT_TWO_PIN)
		assertFalse(deck4.isRightThree())

		val deck5: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN)
		assertFalse(deck5.isRightThree())

		val emptyDeck: Set<Pin> = emptySet()
		assertFalse(emptyDeck.isRightThree())
	}

	@Test
	fun testisThree() {
		val deck1: Set<Pin> = setOf(Pin.RIGHT_THREE_PIN)
		assertTrue(deck1.isThree())

		val deck2: Set<Pin> = setOf(Pin.LEFT_THREE_PIN)
		assertTrue(deck2.isThree())

		val deck3: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.isThree())

		val deck4: Set<Pin> = setOf(Pin.LEFT_TWO_PIN)
		assertFalse(deck4.isThree())

		val deck5: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN)
		assertFalse(deck5.isThree())

		val emptyDeck: Set<Pin> = emptySet()
		assertFalse(emptyDeck.isThree())
	}

	@Test
	fun testArePinsCleared() {
		val deck1 = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertTrue(deck1.arePinsCleared())

		val deck2 = Pin.fullDeck()
		assertTrue(deck2.arePinsCleared())

		val deck3 = setOf(Pin.HEAD_PIN)
		assertFalse(deck3.arePinsCleared())

		val deck4 = setOf(Pin.HEAD_PIN, Pin.HEAD_PIN, Pin.HEAD_PIN, Pin.HEAD_PIN, Pin.HEAD_PIN)
		assertFalse(deck4.arePinsCleared())

		val emptyDeck = setOf<Pin>()
		assertFalse(emptyDeck.arePinsCleared())
	}

	@Test
	fun testDisplayAt() {
		val headPin: Set<Pin> = setOf(Pin.HEAD_PIN)
		assertEquals(headPin.displayAt(rollIndex = 0), "HP")
		assertEquals(headPin.displayAt(rollIndex = 1), "5")
		assertEquals(headPin.displayAt(rollIndex = 2), "5")

		val headPinLeft2: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_TWO_PIN)
		assertEquals(headPinLeft2.displayAt(rollIndex = 0), "H2")
		assertEquals(headPinLeft2.displayAt(rollIndex = 1), "7")
		assertEquals(headPinLeft2.displayAt(rollIndex = 2), "7")

		val headPinRight2: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.RIGHT_TWO_PIN)
		assertEquals(headPinRight2.displayAt(rollIndex = 0), "H2")
		assertEquals(headPinRight2.displayAt(rollIndex = 1), "7")
		assertEquals(headPinRight2.displayAt(rollIndex = 2), "7")

		val leftSplit: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN)
		assertEquals(leftSplit.displayAt(rollIndex = 0), "HS")
		assertEquals(leftSplit.displayAt(rollIndex = 1), "8")
		assertEquals(leftSplit.displayAt(rollIndex = 2), "8")

		val leftSplitWithBonus: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertEquals(leftSplitWithBonus.displayAt(rollIndex = 0), "10")
		assertEquals(leftSplitWithBonus.displayAt(rollIndex = 1), "10")
		assertEquals(leftSplitWithBonus.displayAt(rollIndex = 2), "10")

		val rightSplit: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN)
		assertEquals(rightSplit.displayAt(rollIndex = 0), "HS")
		assertEquals(rightSplit.displayAt(rollIndex = 1), "8")
		assertEquals(rightSplit.displayAt(rollIndex = 2), "8")

		val rightSplitWithBonus: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertEquals(rightSplitWithBonus.displayAt(rollIndex = 0), "10")
		assertEquals(rightSplitWithBonus.displayAt(rollIndex = 1), "10")
		assertEquals(rightSplitWithBonus.displayAt(rollIndex = 2), "10")

		val leftChop: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertEquals(leftChop.displayAt(rollIndex = 0), "C/O")
		assertEquals(leftChop.displayAt(rollIndex = 1), "10")
		assertEquals(leftChop.displayAt(rollIndex = 2), "10")

		val rightChop: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertEquals(rightChop.displayAt(rollIndex = 0), "C/O")
		assertEquals(rightChop.displayAt(rollIndex = 1), "10")
		assertEquals(rightChop.displayAt(rollIndex = 2), "10")

		val ace: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN)
		assertEquals(ace.displayAt(rollIndex = 0), "A")
		assertEquals(ace.displayAt(rollIndex = 1), "11")
		assertEquals(ace.displayAt(rollIndex = 2), "11")

		val left: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_TWO_PIN)
		assertEquals(left.displayAt(rollIndex = 0), "L")
		assertEquals(left.displayAt(rollIndex = 1), "13")
		assertEquals(left.displayAt(rollIndex = 2), "13")

		val right: Set<Pin> = setOf(Pin.HEAD_PIN, Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN, Pin.LEFT_TWO_PIN)
		assertEquals(right.displayAt(rollIndex = 0), "R")
		assertEquals(right.displayAt(rollIndex = 1), "13")
		assertEquals(right.displayAt(rollIndex = 2), "13")

		val fullDeck: Set<Pin> = Pin.fullDeck()
		assertEquals(fullDeck.displayAt(rollIndex = 0), "X")
		assertEquals(fullDeck.displayAt(rollIndex = 1), "/")
		assertEquals(fullDeck.displayAt(rollIndex = 2), "15")

		val leftFive: Set<Pin> = setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN)
		assertEquals(leftFive.displayAt(rollIndex = 0), "5")
		assertEquals(leftFive.displayAt(rollIndex = 1), "5")
		assertEquals(leftFive.displayAt(rollIndex = 2), "5")

		val rightFive: Set<Pin> = setOf(Pin.RIGHT_TWO_PIN, Pin.RIGHT_THREE_PIN)
		assertEquals(rightFive.displayAt(rollIndex = 0), "5")
		assertEquals(rightFive.displayAt(rollIndex = 1), "5")
		assertEquals(rightFive.displayAt(rollIndex = 2), "5")

		val emptyDeck: Set<Pin> = setOf()
		assertEquals(emptyDeck.displayAt(rollIndex = 0), "-")
		assertEquals(emptyDeck.displayAt(rollIndex = 1), "-")
		assertEquals(emptyDeck.displayAt(rollIndex = 2), "-")
	}
}
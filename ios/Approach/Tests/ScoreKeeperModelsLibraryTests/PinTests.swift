@testable import ScoreKeeperModelsLibrary
import XCTest

final class PinTests: XCTestCase {
	func testValue() {
		XCTAssertEqual(Pin.headPin.value, 5)
		XCTAssertEqual(Pin.leftTwoPin.value, 2)
		XCTAssertEqual(Pin.leftThreePin.value, 3)
		XCTAssertEqual(Pin.rightTwoPin.value, 2)
		XCTAssertEqual(Pin.rightThreePin.value, 3)
	}

	func testIsHeadPin() {
		let deck1: Set<Pin> = [.headPin]
		XCTAssertTrue(deck1.isHeadPin)

		let deck2: Set<Pin> = [.headPin, .leftTwoPin]
		XCTAssertFalse(deck2.isHeadPin)
		let deck3: Set<Pin> = [.headPin, .rightTwoPin]
		XCTAssertFalse(deck3.isHeadPin)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isHeadPin)
	}

	func testIsHeadPin2() {
		let deck1: Set<Pin> = [.headPin, .leftTwoPin]
		XCTAssertTrue(deck1.isHeadPin2)
		let deck2: Set<Pin> = [.headPin, .rightTwoPin]
		XCTAssertTrue(deck2.isHeadPin2)

		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isHeadPin2)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isHeadPin2)
	}

	func testIsLeft() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin, .rightTwoPin]
		XCTAssertTrue(deck1.isLeft)

		let deck2: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck2.isLeft)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isLeft)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isLeft)
	}

	func testIsRight() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin, .leftTwoPin]
		XCTAssertTrue(deck1.isRight)

		let deck2: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin, .rightTwoPin]
		XCTAssertFalse(deck2.isRight)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isRight)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isRight)
	}

	func testIsAce() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin]
		XCTAssertTrue(deck1.isAce)

		let deck2: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck2.isAce)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isAce)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isAce)
	}

	func testIsLeftChopOff() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin, .leftTwoPin]
		XCTAssertTrue(deck1.isLeftChopOff)

		let deck2: Set<Pin> = [.headPin, .rightThreePin, .rightTwoPin]
		XCTAssertFalse(deck2.isLeftChopOff)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isLeftChopOff)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isLeftChopOff)
	}

	func testIsRightChopOff() {
		let deck1: Set<Pin> = [.headPin, .rightThreePin, .rightTwoPin]
		XCTAssertTrue(deck1.isRightChopOff)

		let deck2: Set<Pin> = [.headPin, .leftThreePin, .leftTwoPin]
		XCTAssertFalse(deck2.isRightChopOff)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isRightChopOff)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isRightChopOff)
	}

	func testIsChopOff() {
		let deck1: Set<Pin> = [.headPin, .rightThreePin, .rightTwoPin]
		XCTAssertTrue(deck1.isChopOff)
		let deck2: Set<Pin> = [.headPin, .leftThreePin, .leftTwoPin]
		XCTAssertTrue(deck2.isChopOff)

		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isChopOff)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isChopOff)
	}

	func testIsLeftSplit() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertTrue(deck1.isLeftSplit)

		let deck2: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertFalse(deck2.isLeftSplit)
		let deck3: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck3.isLeftSplit)
		let deck4: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertFalse(deck4.isLeftSplit)
		let deck5: Set<Pin> = [.headPin]
		XCTAssertFalse(deck5.isLeftSplit)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isLeftSplit)
	}

	func testIsLeftSplitWithBonus() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertTrue(deck1.isLeftSplitWithBonus)

		let deck2: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertFalse(deck2.isLeftSplitWithBonus)
		let deck3: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck3.isLeftSplitWithBonus)
		let deck4: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertFalse(deck4.isLeftSplitWithBonus)
		let deck5: Set<Pin> = [.headPin]
		XCTAssertFalse(deck5.isLeftSplitWithBonus)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isLeftSplitWithBonus)
	}

	func testIsRightSplit() {
		let deck1: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertTrue(deck1.isRightSplit)

		let deck2: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertFalse(deck2.isRightSplit)
		let deck3: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertFalse(deck3.isRightSplit)
		let deck4: Set<Pin> = [.headPin, .leftThreePin, .leftTwoPin]
		XCTAssertFalse(deck4.isRightSplit)
		let deck5: Set<Pin> = [.headPin]
		XCTAssertFalse(deck5.isRightSplit)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isRightSplit)
	}

	func testIsRightSplitWithBonus() {
		let deck1: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertTrue(deck1.isRightSplitWithBonus)

		let deck2: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertFalse(deck2.isRightSplitWithBonus)
		let deck3: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertFalse(deck3.isRightSplitWithBonus)
		let deck4: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertFalse(deck4.isRightSplitWithBonus)
		let deck5: Set<Pin> = [.headPin]
		XCTAssertFalse(deck5.isRightSplitWithBonus)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isRightSplitWithBonus)
	}

	func testIsSplit() {
		let deck1: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertTrue(deck1.isSplit)
		let deck2: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertTrue(deck2.isSplit)

		let deck3: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertFalse(deck3.isSplit)
		let deck4: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck4.isSplit)
		let deck5: Set<Pin> = [.headPin]
		XCTAssertFalse(deck5.isSplit)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isSplit)
	}

	func testIsSplitWithBonus() {
		let deck1: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertTrue(deck1.isSplitWithBonus)
		let deck2: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertTrue(deck2.isSplitWithBonus)

		let deck3: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertFalse(deck3.isSplitWithBonus)
		let deck4: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertFalse(deck4.isSplitWithBonus)
		let deck5: Set<Pin> = [.headPin]
		XCTAssertFalse(deck5.isSplitWithBonus)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isSplitWithBonus)
	}

	func testIsHitLeftOfMiddle() {
		let deck1: Set<Pin> = [.leftTwoPin]
		XCTAssertTrue(deck1.isHitLeftOfMiddle)
		let deck2: Set<Pin> = [.leftThreePin]
		XCTAssertTrue(deck2.isHitLeftOfMiddle)
		let deck3: Set<Pin> = [.leftTwoPin, .leftThreePin]
		XCTAssertTrue(deck3.isHitLeftOfMiddle)
		let deck4: Set<Pin> = [.leftTwoPin, .leftThreePin, .rightTwoPin]
		XCTAssertTrue(deck4.isHitLeftOfMiddle)

		let deck5: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertFalse(deck5.isHitLeftOfMiddle)
		let deck6: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertFalse(deck6.isHitLeftOfMiddle)
		let deck7: Set<Pin> = [.headPin]
		XCTAssertFalse(deck7.isHitLeftOfMiddle)
		let deck8: Set<Pin> = [.leftThreePin, .rightThreePin]
		XCTAssertFalse(deck8.isHitLeftOfMiddle)
		let deck9: Set<Pin> = [.rightThreePin]
		XCTAssertFalse(deck9.isHitLeftOfMiddle)
		let deck10: Set<Pin> = [.rightTwoPin]
		XCTAssertFalse(deck10.isHitLeftOfMiddle)
		let deck11: Set<Pin> = [.rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck11.isHitLeftOfMiddle)
		let deck12: Set<Pin> = [.leftTwoPin, .leftThreePin, .rightThreePin]
		XCTAssertFalse(deck12.isHitLeftOfMiddle)
		let deck13: Set<Pin> = [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin]
		XCTAssertFalse(deck13.isHitLeftOfMiddle)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isHitLeftOfMiddle)
	}

	func testIsHitRightOfMiddle() {
		let deck1: Set<Pin> = [.rightTwoPin]
		XCTAssertTrue(deck1.isHitRightOfMiddle)
		let deck2: Set<Pin> = [.rightThreePin]
		XCTAssertTrue(deck2.isHitRightOfMiddle)
		let deck3: Set<Pin> = [.rightTwoPin, .rightThreePin]
		XCTAssertTrue(deck3.isHitRightOfMiddle)
		let deck4: Set<Pin> = [.rightTwoPin, .rightThreePin, .leftTwoPin]
		XCTAssertTrue(deck4.isHitRightOfMiddle)

		let deck5: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertFalse(deck5.isHitRightOfMiddle)
		let deck6: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertFalse(deck6.isHitRightOfMiddle)
		let deck7: Set<Pin> = [.headPin]
		XCTAssertFalse(deck7.isHitRightOfMiddle)
		let deck8: Set<Pin> = [.rightThreePin, .leftThreePin]
		XCTAssertFalse(deck8.isHitRightOfMiddle)
		let deck9: Set<Pin> = [.leftThreePin]
		XCTAssertFalse(deck9.isHitRightOfMiddle)
		let deck10: Set<Pin> = [.leftTwoPin]
		XCTAssertFalse(deck10.isHitRightOfMiddle)
		let deck11: Set<Pin> = [.leftThreePin, .rightTwoPin]
		XCTAssertFalse(deck11.isHitRightOfMiddle)
		let deck12: Set<Pin> = [.rightTwoPin, .rightThreePin, .leftThreePin]
		XCTAssertFalse(deck12.isHitRightOfMiddle)
		let deck13: Set<Pin> = [.rightTwoPin, .rightThreePin, .leftTwoPin, .leftThreePin]
		XCTAssertFalse(deck13.isHitRightOfMiddle)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isHitRightOfMiddle)
	}

	func testIsMiddleHit() {
		let deck1: Set<Pin> = [.headPin]
		XCTAssertTrue(deck1.isMiddleHit)
		let deck2: Set<Pin> = [.headPin, .leftTwoPin]
		XCTAssertTrue(deck2.isMiddleHit)
		let deck3: Set<Pin> = [.headPin, .rightTwoPin]
		XCTAssertTrue(deck3.isMiddleHit)
		let deck4: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertTrue(deck4.isMiddleHit)
		let deck5: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertTrue(deck5.isMiddleHit)
		let deck6: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertTrue(deck6.isMiddleHit)
		let deck7: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertTrue(deck7.isMiddleHit)

		let deck8: Set<Pin> = [.rightThreePin, .leftThreePin]
		XCTAssertFalse(deck8.isMiddleHit)
		let deck9: Set<Pin> = [.leftThreePin]
		XCTAssertFalse(deck9.isMiddleHit)
		let deck10: Set<Pin> = [.leftTwoPin]
		XCTAssertFalse(deck10.isMiddleHit)
		let deck11: Set<Pin> = [.leftThreePin, .rightTwoPin]
		XCTAssertFalse(deck11.isMiddleHit)
		let deck12: Set<Pin> = [.rightTwoPin, .rightThreePin, .leftThreePin]
		XCTAssertFalse(deck12.isMiddleHit)
		let deck13: Set<Pin> = [.rightTwoPin, .rightThreePin, .leftTwoPin, .leftThreePin]
		XCTAssertFalse(deck13.isMiddleHit)
		let deck14: Set<Pin> = [.leftThreePin, .rightThreePin]
		XCTAssertFalse(deck14.isMiddleHit)
		let deck15: Set<Pin> = [.rightThreePin]
		XCTAssertFalse(deck15.isMiddleHit)
		let deck16: Set<Pin> = [.rightTwoPin]
		XCTAssertFalse(deck16.isMiddleHit)
		let deck17: Set<Pin> = [.rightThreePin, .leftTwoPin]
		XCTAssertFalse(deck17.isMiddleHit)
		let deck18: Set<Pin> = [.leftTwoPin, .leftThreePin, .rightThreePin]
		XCTAssertFalse(deck18.isMiddleHit)
		let deck19: Set<Pin> = [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin]
		XCTAssertFalse(deck19.isMiddleHit)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isMiddleHit)
	}

	func testIsLeftTwelve() {
		let deck1: Set<Pin> = [.headPin, .rightTwoPin, .leftThreePin, .leftTwoPin]
		XCTAssertTrue(deck1.isLeftTwelve)

		let deck2: Set<Pin> = [.headPin, .leftTwoPin, .rightThreePin, .rightTwoPin]
		XCTAssertFalse(deck2.isLeftTwelve)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isLeftTwelve)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isLeftTwelve)
	}

	func testIsRightTwelve() {
		let deck1: Set<Pin> = [.headPin, .leftTwoPin, .rightThreePin, .rightTwoPin]
		XCTAssertTrue(deck1.isRightTwelve)

		let deck2: Set<Pin> = [.headPin, .rightTwoPin, .leftThreePin, .leftTwoPin]
		XCTAssertFalse(deck2.isRightTwelve)
		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isRightTwelve)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isRightTwelve)
	}

	func testIsTwelve() {
		let deck1: Set<Pin> = [.headPin, .rightTwoPin, .leftThreePin, .leftTwoPin]
		XCTAssertTrue(deck1.isTwelve)
		let deck2: Set<Pin> = [.headPin, .leftTwoPin, .rightThreePin, .rightTwoPin]
		XCTAssertTrue(deck2.isTwelve)

		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.isTwelve)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.isTwelve)
	}

	func testArePinsCleared() {
		let deck1: Set<Pin> = [.headPin, .leftThreePin, .leftTwoPin, .rightThreePin, .rightTwoPin]
		XCTAssertTrue(deck1.arePinsCleared)
		let deck2: Set<Pin> = Pin.fullDeck
		XCTAssertTrue(deck2.arePinsCleared)

		let deck3: Set<Pin> = [.headPin]
		XCTAssertFalse(deck3.arePinsCleared)
		let deck4: Set<Pin> = [.headPin, .headPin, .headPin, .headPin, .headPin]
		XCTAssertFalse(deck4.arePinsCleared)

		let emptyDeck: Set<Pin> = []
		XCTAssertFalse(emptyDeck.arePinsCleared)
	}

	func testDisplayValue() {
		let headPin: Set<Pin> = [.headPin]
		XCTAssertEqual(headPin.displayValue(rollIndex: 0), "HP")
		XCTAssertEqual(headPin.displayValue(rollIndex: 1), "5")
		XCTAssertEqual(headPin.displayValue(rollIndex: 2), "5")

		let headPinLeft2: Set<Pin> = [.headPin, .leftTwoPin]
		XCTAssertEqual(headPinLeft2.displayValue(rollIndex: 0), "H2")
		XCTAssertEqual(headPinLeft2.displayValue(rollIndex: 1), "7")
		XCTAssertEqual(headPinLeft2.displayValue(rollIndex: 2), "7")

		let headPinRight2: Set<Pin> = [.headPin, .rightTwoPin]
		XCTAssertEqual(headPinRight2.displayValue(rollIndex: 0), "H2")
		XCTAssertEqual(headPinRight2.displayValue(rollIndex: 1), "7")
		XCTAssertEqual(headPinRight2.displayValue(rollIndex: 2), "7")

		let leftSplit: Set<Pin> = [.headPin, .leftThreePin]
		XCTAssertEqual(leftSplit.displayValue(rollIndex: 0), "HS")
		XCTAssertEqual(leftSplit.displayValue(rollIndex: 1), "8")
		XCTAssertEqual(leftSplit.displayValue(rollIndex: 2), "8")

		let leftSplitWithBonus: Set<Pin> = [.headPin, .leftThreePin, .rightTwoPin]
		XCTAssertEqual(leftSplitWithBonus.displayValue(rollIndex: 0), "10")
		XCTAssertEqual(leftSplitWithBonus.displayValue(rollIndex: 1), "10")
		XCTAssertEqual(leftSplitWithBonus.displayValue(rollIndex: 2), "10")

		let rightSplit: Set<Pin> = [.headPin, .rightThreePin]
		XCTAssertEqual(rightSplit.displayValue(rollIndex: 0), "HS")
		XCTAssertEqual(rightSplit.displayValue(rollIndex: 1), "8")
		XCTAssertEqual(rightSplit.displayValue(rollIndex: 2), "8")

		let rightSplitWithBonus: Set<Pin> = [.headPin, .rightThreePin, .leftTwoPin]
		XCTAssertEqual(rightSplitWithBonus.displayValue(rollIndex: 0), "10")
		XCTAssertEqual(rightSplitWithBonus.displayValue(rollIndex: 1), "10")
		XCTAssertEqual(rightSplitWithBonus.displayValue(rollIndex: 2), "10")

		let leftChop: Set<Pin> = [.headPin, .leftThreePin, .leftTwoPin]
		XCTAssertEqual(leftChop.displayValue(rollIndex: 0), "C/O")
		XCTAssertEqual(leftChop.displayValue(rollIndex: 1), "10")
		XCTAssertEqual(leftChop.displayValue(rollIndex: 2), "10")

		let rightChop: Set<Pin> = [.headPin, .rightThreePin, .rightTwoPin]
		XCTAssertEqual(rightChop.displayValue(rollIndex: 0), "C/O")
		XCTAssertEqual(rightChop.displayValue(rollIndex: 1), "10")
		XCTAssertEqual(rightChop.displayValue(rollIndex: 2), "10")

		let ace: Set<Pin> = [.headPin, .rightThreePin, .leftThreePin]
		XCTAssertEqual(ace.displayValue(rollIndex: 0), "A")
		XCTAssertEqual(ace.displayValue(rollIndex: 1), "11")
		XCTAssertEqual(ace.displayValue(rollIndex: 2), "11")

		let left: Set<Pin> = [.headPin, .rightThreePin, .leftThreePin, .rightTwoPin]
		XCTAssertEqual(left.displayValue(rollIndex: 0), "L")
		XCTAssertEqual(left.displayValue(rollIndex: 1), "13")
		XCTAssertEqual(left.displayValue(rollIndex: 2), "13")

		let right: Set<Pin> = [.headPin, .leftThreePin, .rightThreePin, .leftTwoPin]
		XCTAssertEqual(right.displayValue(rollIndex: 0), "R")
		XCTAssertEqual(right.displayValue(rollIndex: 1), "13")
		XCTAssertEqual(right.displayValue(rollIndex: 2), "13")

		let fullDeck: Set<Pin> = Pin.fullDeck
		XCTAssertEqual(fullDeck.displayValue(rollIndex: 0), "X")
		XCTAssertEqual(fullDeck.displayValue(rollIndex: 1), "/")
		XCTAssertEqual(fullDeck.displayValue(rollIndex: 2), "15")

		let leftFive: Set<Pin> = [.leftTwoPin, .leftThreePin]
		XCTAssertEqual(leftFive.displayValue(rollIndex: 0), "5")
		XCTAssertEqual(leftFive.displayValue(rollIndex: 1), "5")
		XCTAssertEqual(leftFive.displayValue(rollIndex: 2), "5")

		let rightFive: Set<Pin> = [.leftTwoPin, .leftThreePin]
		XCTAssertEqual(rightFive.displayValue(rollIndex: 0), "5")
		XCTAssertEqual(rightFive.displayValue(rollIndex: 1), "5")
		XCTAssertEqual(rightFive.displayValue(rollIndex: 2), "5")

		let emptyDeck: Set<Pin> = []
		XCTAssertEqual(emptyDeck.displayValue(rollIndex: 0), "-")
		XCTAssertEqual(emptyDeck.displayValue(rollIndex: 1), "-")
		XCTAssertEqual(emptyDeck.displayValue(rollIndex: 2), "-")
	}
}

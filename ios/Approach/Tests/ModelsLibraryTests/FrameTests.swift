import Dependencies
@testable import ModelsLibrary
import XCTest

final class FrameTests: XCTestCase {

	// MARK: - First Rolls

	func testFirstRolls_InAnyFrame_ReturnsFirstRoll() {
		let frame1 = Frame.Summary(
			index: 0,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame1.firstRolls,
			[.init(index: 0, roll: .init(pinsDowned: [.headPin]))]
		)

		let frame2 = Frame.Summary(
			index: 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
			]
		)

		XCTAssertEqual(
			frame2.firstRolls,
			[.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin]))]
		)

		let frame3 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame3.firstRolls,
			[.init(index: 0, roll: .init(pinsDowned: [.headPin]))]
		)
	}

	func testFirstRolls_WithNoRolls_ReturnsEmptyArray() {
		let frame = Frame.Summary(index: 0, rolls: [])

		XCTAssertEqual(frame.firstRolls, [])
	}

	func testFirstRolls_InLastFrame_WithNoStrikesOrSpares_ReturnsFirstRoll() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.firstRolls, [.init(index: 0, roll: .init(pinsDowned: []))])
	}

	func testFirstRolls_InLastFrame_WithOneStrike_ReturnsTwoRolls() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.firstRolls, [
			.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
		])
	}

	func testFirstRolls_InLastFrame_WithOneSpare_ReturnsTwoRolls() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.firstRolls, [
			.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
			.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
		])
	}

	func testFirstRolls_InLastFrame_WithTwoStrikes_ReturnsThreeRolls() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.firstRolls, [
			.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
		])
	}

	func testFirstRolls_InLastFrame_WithThreeStrikes_ReturnsThreeRolls() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.firstRolls, [
			.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
		])
	}

	// MARK: - Second Rolls

	func testSecondRolls_InAnyFrame_ReturnsSecondRoll() {
		let frame1 = Frame.Summary(
			index: 0,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame1.secondRolls,
			[.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))]
		)

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame2.secondRolls,
			[.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))]
		)
	}

	func testSecondRolls_WithNoRolls_ReturnsEmptyArray() {
		let frame = Frame.Summary(index: 0, rolls: [])

		XCTAssertEqual(frame.secondRolls, [])
	}

	func testSecondRolls_WithNoSecondRolls_ReturnsEmptyArray() {
		let frame = Frame.Summary(index: 0, rolls: [.init(index: 0, roll: .default)])

		XCTAssertEqual(frame.secondRolls, [])
	}

	func testSecondRolls_InLastFrame_WithNoStrikesOrSpares_ReturnsSecondRoll() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.secondRolls, [.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))])
	}

	func testSecondRolls_InLastFrame_WithOneStrike_ReturnsOneRoll() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.secondRolls, [
			.init(index: 2, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
		])
	}

	func testSecondRolls_InLastFrame_WithOneSpare_ReturnsOneRoll() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.secondRolls, [
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
		])
	}

	func testSecondRolls_InLastFrame_WithTwoStrikes_ReturnsNoRolls() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.secondRolls, [])
	}

	func testSecondRolls_InLastFrame_WithThreeStrikes_ReturnsNoRolls() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.secondRolls, [])
	}

	// MARK: - Roll Pairs

	func testRollPairs_InAnyFrame_ReturnsPairs() {
		let frame1 = Frame.Summary(
			index: 0,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame1.rollPairs,
			[
				.init(
					firstRoll: .init(index: 0, roll: .init(pinsDowned: [.headPin])),
					secondRoll: .init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))
				)
			]
		)

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame2.rollPairs,
			[
				.init(
					firstRoll: .init(index: 0, roll: .init(pinsDowned: [.headPin])),
					secondRoll: .init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))
				)
			]
		)
	}

	func testRollPairs_WithNoRolls_ReturnsNothing() {
		let frame = Frame.Summary(index: 0, rolls: [])

		XCTAssertEqual(frame.rollPairs, [])
	}

	func testRollPairs_WithNoSecondRolls_ReturnsNothing() {
		let frame = Frame.Summary(index: 0, rolls: [.init(index: 0, roll: .default)])

		XCTAssertEqual(frame.rollPairs, [])
	}

	func testRollPairs_InLastFrame_WithNoStrikesOrSpares_ReturnsOnePair() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame.rollPairs,
			[
				.init(
					firstRoll: .init(index: 0, roll: .init(pinsDowned: [])),
					secondRoll: .init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))
				),
			]
		)
	}

	func testRollPairs_InLastFrame_WithOneStrike_ReturnsOnePair() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame.rollPairs,
			[
				.init(
					firstRoll: .init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
					secondRoll: .init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin]))
				),
			]
		)
	}

	func testRollPairs_InLastFrame_WithOneSpare_ReturnsOnePair() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(
			frame.rollPairs,
			[
				.init(
					firstRoll: .init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
					secondRoll: .init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))
				),
			]
		)
	}

	func testRollPairs_InLastFrame_WithTwoStrikes_ReturnsNoPairs() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.rollPairs, [])
	}

	func testRollPairs_InLastFrame_WithThreeStrikes_ReturnsNoPairs() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.rollPairs, [])
	}

	// MARK: - Pins Left On Deck

	func testPinsLeftOnDeck_CountsHeadPins() {
		let frame = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin, .rightTwoPin, .rightThreePin])),
		])

		XCTAssertEqual(frame.pinsLeftOnDeck, [.headPin])
	}

	func testPinsLeftOnDeck_CountsTwoPins() {
		let frame = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin, .headPin])),
		])

		XCTAssertEqual(frame.pinsLeftOnDeck, [.leftTwoPin, .rightTwoPin])
	}

	func testPinsLeftOnDeck_CountsThreePins() {
		let frame = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin, .headPin])),
		])

		XCTAssertEqual(frame.pinsLeftOnDeck, [.leftThreePin, .rightThreePin])
	}

	func testPinsLeftOnDeck_CountsMultiplePins() {
		let frame = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [])),
		])

		XCTAssertEqual(frame.pinsLeftOnDeck, [.leftTwoPin, .rightTwoPin, .leftThreePin, .rightThreePin, .headPin])
	}

	func testPinsLeftOnDeck_CountsFramesWithOneRoll() {
		let frame = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.headPin])),
		])

		XCTAssertEqual(frame.pinsLeftOnDeck, [.leftTwoPin, .rightTwoPin, .leftThreePin, .rightThreePin])
	}

	func testPinsLeftOnDeck_CountsFramesWithTwoRolls() {
		let frame1 = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.headPin])),
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
		])

		XCTAssertEqual(frame1.pinsLeftOnDeck, [.leftThreePin, .rightThreePin])

		let frame2 = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.headPin])),
			.init(index: 1, roll: .init(pinsDowned: [])),
		])

		XCTAssertEqual(frame2.pinsLeftOnDeck, [.leftThreePin, .rightThreePin, .leftTwoPin, .rightTwoPin])
	}

	func testPinsLeftOnDeck_CountsFramesWithThreeRolls() {
		let frame1 = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.headPin])),
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
			.init(index: 2, roll: .init(pinsDowned: [])),
		])

		XCTAssertEqual(frame1.pinsLeftOnDeck, [.leftThreePin, .rightThreePin])

		let frame2 = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.headPin])),
			.init(index: 1, roll: .init(pinsDowned: [])),
			.init(index: 2, roll: .init(pinsDowned: [])),
		])

		XCTAssertEqual(frame2.pinsLeftOnDeck, [.leftThreePin, .rightThreePin, .leftTwoPin, .rightTwoPin])

		let frame3 = Frame.Summary(rolls: [
			.init(index: 0, roll: .init(pinsDowned: [.headPin])),
			.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
			.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
		])

		XCTAssertEqual(frame3.pinsLeftOnDeck, [])
	}

	func testPinsLeftOnDeck_InLastFrame_CountsPinsAfterOpenFrame() {
		let frame1 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
				.init(index: 2, roll: .init(pinsDowned: [])),
			]
		)

		XCTAssertEqual(frame1.pinsLeftOnDeck, [.leftThreePin, .rightThreePin])

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame2.pinsLeftOnDeck, [])

		let frame3 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [])),
				.init(index: 1, roll: .init(pinsDowned: [])),
				.init(index: 2, roll: .init(pinsDowned: [])),
			]
		)

		XCTAssertEqual(frame3.pinsLeftOnDeck, [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])
	}

	func testPinsLeftOnDeck_InLastFrame_CountsPinsAfterStrike() {
		let frame1 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [])),
				.init(index: 2, roll: .init(pinsDowned: [])),
			]
		)

		XCTAssertEqual(frame1.pinsLeftOnDeck, [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame2.pinsLeftOnDeck, [.headPin])

		let frame3 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin])),
				.init(index: 2, roll: .init(pinsDowned: [])),
			]
		)

		XCTAssertEqual(frame3.pinsLeftOnDeck, [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])
	}

	func testPinsLeftOnDeck_InLastFrame_CountsPinsAfterSpare() {
		let frame1 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [])),
			]
		)

		XCTAssertEqual(frame1.pinsLeftOnDeck, [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame2.pinsLeftOnDeck, [.headPin, .rightTwoPin, .leftTwoPin])

		let frame3 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin])),
				.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.headPin])),
			]
		)

		XCTAssertEqual(frame3.pinsLeftOnDeck, [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])
	}

	func testPinsLeftOnDeck_InLastFrame_CountsPinsAfterTwoStrikes() {
		let frame1 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [])),
			]
		)

		XCTAssertEqual(frame1.pinsLeftOnDeck, [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame2.pinsLeftOnDeck, [.headPin, .leftTwoPin, .rightTwoPin])

		let frame3 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.headPin])),
			]
		)

		XCTAssertEqual(frame3.pinsLeftOnDeck, [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])
	}

	func testPinsLeftOnDeck_InLastFrame_CountsPinsAfterOneStrikeOneSpare() {
		let frame1 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin])),
				.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame1.pinsLeftOnDeck, [])

		let frame2 = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame2.pinsLeftOnDeck, [])
	}

	func testPinsLeftOnDeck_InLastFrame_CountsPinsAfterThreeStrikes() {
		let frame = Frame.Summary(
			index: Game.NUMBER_OF_FRAMES - 1,
			rolls: [
				.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
				.init(index: 2, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
			]
		)

		XCTAssertEqual(frame.pinsLeftOnDeck, [])
	}
}

extension Frame.Summary {
	init(index: Int = 0, rolls: [Frame.OrderedRoll]) {
		self.init(gameId: UUID(0), index: index, rolls: rolls)
	}
}

extension Frame.OrderedRoll {
		init(index: Int, roll: Frame.Roll) {
				self.init(index: index, roll: roll, bowlingBall: nil)
		}
}

extension Frame.Roll {
		init(pinsDowned: Set<Pin>) {
				self.init(pinsDowned: pinsDowned, didFoul: false)
		}
}

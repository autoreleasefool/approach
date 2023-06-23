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
}

extension Frame.Summary {
	init(index: Int, rolls: [Frame.OrderedRoll]) {
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

import Dependencies
@testable import ModelsLibrary
@testable import ScoringService
@testable import ScoringServiceInterface
import XCTest

@MainActor
final class ScoringServiceTests: XCTestCase {
	@Dependency(\.scoringService) var scoring

	func testCalculatesScoreForFrames() {
		// TODO: testCalculatesScoreForFrames
		XCTFail("TODO")
	}

	func testCalculatesScoreForFramesWithSteps() async {
		let frames: [[Frame.OrderedRoll]] = [
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin], didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin], didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin], didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: true), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil),
			],
		]

		let steps = await withDependencies {
			$0.scoringService = .liveValue
		} operation: {
			await scoring.calculateScoreForFramesWithSteps(frames)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 25),
			.init(index: 1, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 40),
			.init(index: 2, rolls: [.init(index: 0, display: "C/O", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 70),
			.init(index: 3, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "13", didFoul: false)], score: 113),
			.init(index: 4, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "13", didFoul: false), .init(index: 2, display: "2", didFoul: false)], score: 143),
			.init(index: 5, rolls: [.init(index: 0, display: "R", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 163),
			.init(index: 6, rolls: [.init(index: 0, display: "5", didFoul: true), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 153),
			.init(index: 7, rolls: [.init(index: 0, display: "HP", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 158),
			.init(index: 8, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 203),
			.init(index: 9, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "X", didFoul: false), .init(index: 2, display: "HP", didFoul: false)], score: 238),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}

	func testCalculator() async {
		let frames: [[Frame.OrderedRoll]] = [
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin], didFoul: false), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
			],
			[],
			[],
			[],
			[],
			[],
			[],
			[],
			[],
		]

		let steps = await withDependencies {
			$0.scoringService = .liveValue
		} operation: {
			await scoring.calculateScoreForFramesWithSteps(frames)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "10", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 25),
			.init(index: 1, rolls: [.init(index: 0, display: "10", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 35),
			.init(index: 2, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 3, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 4, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 5, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 6, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 7, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 8, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
			.init(index: 9, rolls: [.init(index: 0, display: nil, didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: nil),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}

	func testScoresStrikeCorrectly() {
		// TODO: testScoresStrikeCorrectly
		XCTFail("TODO")
	}

	func testScoresSpareCorrectly() {
		// TODO: testScoresSpareCorrectly
		XCTFail("TODO")
	}

	func testScoresStrikeInLastFrameCorrectly() {
		// TODO: testScoresStrikeInLastFrameCorrectly
		XCTFail("TODO")
	}

	func testScoresSpareInLastFrameCorrectly() {
		// TODO: testScoresSpareInLastFrameCorrectly
		XCTFail("TODO")
	}

	func testScoresOpenFrameCorrectly() {
		// TODO: testScoresOpenFrameCorrectly
		XCTFail("TODO")
	}

	func testScoreCannotGoNegative() {
		// TODO: testScoreCannotGoNegative
		XCTFail("TODO")
	}
}

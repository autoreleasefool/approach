import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FramesRepository
import FramesRepositoryInterface
@testable import ModelsLibrary
@testable import ScoringService
@testable import ScoringServiceInterface
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class ScoringServiceTests: XCTestCase {
	@Dependency(\.frames) var frames
	@Dependency(\.scoring) var scoring

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
			$0.scoring = .liveValue
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

	func testCalculatesScoreWithInvalidRollsAfterStrike() async {
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
			$0.scoring = .liveValue
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

	func testCalculatesScoreWithEmptyFrames() async {
		let frames: [[Frame.OrderedRoll]] = [
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[],
			[],
			[],
			[],
			[],
			[],
			[],
			[],
			[
				.init(index: 9, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
		]

		let steps = await withDependencies {
			$0.scoring = .liveValue
		} operation: {
			await scoring.calculateScoreForFramesWithSteps(frames)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 1, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 2, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 3, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 4, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 5, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 6, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 7, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 8, rolls: [.init(index: 0, display: "-", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 9, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: 30),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}

	func testCalculatesScoreWithFoulsAppliedCorrectly() async {
		let frames: [[Frame.OrderedRoll]] = [
			[
				.init(index: 0, roll: .init(pinsDowned: [], didFoul: true), bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [], didFoul: true), bowlingBall: nil),
				.init(index: 2, roll: .init(pinsDowned: [], didFoul: true), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: true), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[
				.init(index: 0, roll: .init(pinsDowned: Pin.fullDeck, didFoul: false), bowlingBall: nil),
			],
			[],
			[],
			[],
			[],
			[],
			[],
		]

		let steps = await withDependencies {
			$0.scoring = .liveValue
		} operation: {
			await scoring.calculateScoreForFramesWithSteps(frames)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "-", didFoul: true), .init(index: 1, display: "-", didFoul: true), .init(index: 2, display: "-", didFoul: true)], score: 0),
			.init(index: 1, rolls: [.init(index: 0, display: "X", didFoul: true), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 0),
			.init(index: 2, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 15),
			.init(index: 3, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: nil, didFoul: false), .init(index: 2, display: nil, didFoul: false)], score: 30),
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
		// FIXME: testScoresStrikeCorrectly
		XCTFail("TODO")
	}

	func testScoresSpareCorrectly() {
		// FIXME: testScoresSpareCorrectly
		XCTFail("TODO")
	}

	func testScoresStrikeInLastFrameCorrectly() {
		// FIXME: testScoresStrikeInLastFrameCorrectly
		XCTFail("TODO")
	}

	func testScoresSpareInLastFrameCorrectly() {
		// FIXME: testScoresSpareInLastFrameCorrectly
		XCTFail("TODO")
	}

	func testScoresOpenFrameCorrectly() {
		// FIXME: testScoresOpenFrameCorrectly
		XCTFail("TODO")
	}

	func testScoreCannotGoNegative() {
		// FIXME: testScoreCannotGoNegative
		XCTFail("TODO")
	}

	func testCalculatesScoreExampleGame1() async throws {
		let db = try generatePopulatedDatabase()

		let steps: [ScoreStep] = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
			$0.scoring = .liveValue
		} operation: {
			let frames = try await self.frames.load(UUID(0))
			guard let rolls = frames?.map(\.rolls) else {
				XCTFail("Could not find frames for game 0")
				return []
			}
			return await scoring.calculateScoreForFramesWithSteps(rolls)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "HP", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 15),
			.init(index: 1, rolls: [.init(index: 0, display: "L", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "2", didFoul: false)], score: 30),
			.init(index: 2, rolls: [.init(index: 0, display: "R", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 60),
			.init(index: 3, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "13", didFoul: false), .init(index: 2, display: "2", didFoul: false)], score: 90),
			.init(index: 4, rolls: [.init(index: 0, display: "L", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "13", didFoul: false)], score: 118),
			.init(index: 5, rolls: [.init(index: 0, display: "R", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 131),
			.init(index: 6, rolls: [.init(index: 0, display: "A", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 142),
			.init(index: 7, rolls: [.init(index: 0, display: "C/O", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 162),
			.init(index: 8, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 182),
			.init(index: 9, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 192),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}

	func testCalculatesScoreExampleGame2() async throws {
		let db = try generatePopulatedDatabase()

		let steps: [ScoreStep] = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
			$0.scoring = .liveValue
		} operation: {
			let frames = try await self.frames.load(UUID(1))
			guard let rolls = frames?.map(\.rolls) else {
				XCTFail("Could not find frames for game 1")
				return []
			}
			return await scoring.calculateScoreForFramesWithSteps(rolls)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "H2", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "3", didFoul: false)], score: 15),
			.init(index: 1, rolls: [.init(index: 0, display: "HS", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "2", didFoul: false)], score: 30),
			.init(index: 2, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 75),
			.init(index: 3, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "10", didFoul: false)], score: 115),
			.init(index: 4, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "10", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 140),
			.init(index: 5, rolls: [.init(index: 0, display: "C/O", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 150),
			.init(index: 6, rolls: [.init(index: 0, display: "12", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "12", didFoul: false)], score: 177),
			.init(index: 7, rolls: [.init(index: 0, display: "12", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 189),
			.init(index: 8, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 234),
			.init(index: 9, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "X", didFoul: false), .init(index: 2, display: "HP", didFoul: false)], score: 269),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}

	func testCalculatesScoreExampleGame3() async throws {
		let db = try generatePopulatedDatabase()

		let steps: [ScoreStep] = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
			$0.scoring = .liveValue
		} operation: {
			let frames = try await self.frames.load(UUID(2))
			guard let rolls = frames?.map(\.rolls) else {
				XCTFail("Could not find frames for game 2")
				return []
			}
			return await scoring.calculateScoreForFramesWithSteps(rolls)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "X", didFoul: true), .init(index: 1, display: "15", didFoul: false), .init(index: 2, display: "13", didFoul: false)], score: 28),
			.init(index: 1, rolls: [.init(index: 0, display: "X", didFoul: true), .init(index: 1, display: "13", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 41),
			.init(index: 2, rolls: [.init(index: 0, display: "L", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 54),
			.init(index: 3, rolls: [.init(index: 0, display: "L", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "13", didFoul: false)], score: 82),
			.init(index: 4, rolls: [.init(index: 0, display: "R", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 95),
			.init(index: 5, rolls: [.init(index: 0, display: "R", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 115),
			.init(index: 6, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 125),
			.init(index: 7, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "-", didFoul: false)], score: 135),
			.init(index: 8, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 150),
			.init(index: 9, rolls: [.init(index: 0, display: "5", didFoul: false), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 165),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}

	func testCalculatesScoreExampleGame4() async throws {
		let db = try generatePopulatedDatabase()

		let steps: [ScoreStep] = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
			$0.scoring = .liveValue
		} operation: {
			let frames = try await self.frames.load(UUID(3))
			guard let rolls = frames?.map(\.rolls) else {
				XCTFail("Could not find frames for game 3")
				return []
			}
			return await scoring.calculateScoreForFramesWithSteps(rolls)
		}

		let expectedSteps: [ScoreStep] = [
			.init(index: 0, rolls: [.init(index: 0, display: "A", didFoul: true), .init(index: 1, display: "2", didFoul: false), .init(index: 2, display: "2", didFoul: false)], score: 0),
			.init(index: 1, rolls: [.init(index: 0, display: "HS", didFoul: true), .init(index: 1, display: "5", didFoul: false), .init(index: 2, display: "2", didFoul: false)], score: 0),
			.init(index: 2, rolls: [.init(index: 0, display: "12", didFoul: true), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "12", didFoul: false)], score: 12),
			.init(index: 3, rolls: [.init(index: 0, display: "12", didFoul: true), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "10", didFoul: false)], score: 22),
			.init(index: 4, rolls: [.init(index: 0, display: "C/O", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 37),
			.init(index: 5, rolls: [.init(index: 0, display: "C/O", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "10", didFoul: false)], score: 62),
			.init(index: 6, rolls: [.init(index: 0, display: "C/O", didFoul: false), .init(index: 1, display: "-", didFoul: false), .init(index: 2, display: "5", didFoul: false)], score: 77),
			.init(index: 7, rolls: [.init(index: 0, display: "HS", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "11", didFoul: false)], score: 103),
			.init(index: 8, rolls: [.init(index: 0, display: "A", didFoul: false), .init(index: 1, display: "/", didFoul: false), .init(index: 2, display: "15", didFoul: false)], score: 133),
			.init(index: 9, rolls: [.init(index: 0, display: "X", didFoul: false), .init(index: 1, display: "HS", didFoul: false), .init(index: 2, display: "/", didFoul: false)], score: 163),
		]

		XCTAssertEqual(steps.count, expectedSteps.count)
		for (step, expectedStep) in zip(steps, expectedSteps) {
			XCTAssertEqual(step, expectedStep)
		}
	}
}

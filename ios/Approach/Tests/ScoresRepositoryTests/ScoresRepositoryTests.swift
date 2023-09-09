import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FramesRepository
@testable import FramesRepositoryInterface
import GamesRepository
@testable import GamesRepositoryInterface
@testable import ModelsLibrary
@testable import ScoresRepository
@testable import ScoresRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class ScoresRepositoryTests: XCTestCase {
	@Dependency(\.scores) var scores

	func testCalculatesScoreForFramesWithSteps() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[Frame.Edit], Error>.makeStream()

		let scoresStream = withDependencies {
			$0.games.edit = { id in await self.buildGame(withId: id) }
			$0.frames.observe = { _ in frames }
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let orderedRolls: [[Frame.OrderedRoll]] = [
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

		framesContinuation.yield(Game.FRAME_INDICES.map {
			.init(gameId: UUID(0), index: $0, rolls: orderedRolls.endIndex > $0 ? orderedRolls[$0] : [])
		})

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 25),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 40),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 70),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "13", didFoul: false)], score: 113),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "13", didFoul: false), .init(index: 2, displayValue: "2", didFoul: false)], score: 143),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "R", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 163),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "5", didFoul: true), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 153),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "HP", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 158),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 203),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "X", didFoul: false), .init(index: 2, displayValue: "HP", didFoul: false)], score: 238),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreWithInvalidRollsAfterStrike() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[Frame.Edit], Error>.makeStream()

		let scoresStream = withDependencies {
			$0.games.edit = { id in await self.buildGame(withId: id) }
			$0.frames.observe = { _ in frames }
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let orderedRolls: [[Frame.OrderedRoll]] = [
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

		framesContinuation.yield(Game.FRAME_INDICES.map {
			.init(gameId: UUID(0), index: $0, rolls: orderedRolls.endIndex > $0 ? orderedRolls[$0] : [])
		})

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "10", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 25),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "10", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 35),
				.init(index: 2, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 3, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 4, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 5, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 6, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 7, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 8, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 9, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreWithEmptyFrames() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[Frame.Edit], Error>.makeStream()

		let scoresStream = withDependencies {
			$0.games.edit = { id in await self.buildGame(withId: id) }
			$0.frames.observe = { _ in frames }
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let orderedRolls: [[Frame.OrderedRoll]] = [
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

		framesContinuation.yield(Game.FRAME_INDICES.map {
			.init(gameId: UUID(0), index: $0, rolls: orderedRolls.endIndex > $0 ? orderedRolls[$0] : [])
		})

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "-", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: 30),
			]
		)
	}

	func testCalculatesScoreWithFoulsAppliedCorrectly() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[Frame.Edit], Error>.makeStream()

		let scoresStream = withDependencies {
			$0.games.edit = { id in await self.buildGame(withId: id) }
			$0.frames.observe = { _ in frames }
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let orderedRolls: [[Frame.OrderedRoll]] = [
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

		framesContinuation.yield(Game.FRAME_INDICES.map {
			.init(gameId: UUID(0), index: $0, rolls: orderedRolls.endIndex > $0 ? orderedRolls[$0] : [])
		})

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "-", didFoul: true), .init(index: 1, displayValue: "-", didFoul: true), .init(index: 2, displayValue: "-", didFoul: true)], score: 0),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "X", didFoul: true), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 0),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 15),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: 30),
				.init(index: 4, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 5, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 6, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 7, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 8, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
				.init(index: 9, rolls: [.init(index: 0, displayValue: nil, didFoul: false), .init(index: 1, displayValue: nil, didFoul: false), .init(index: 2, displayValue: nil, didFoul: false)], score: nil),
			]
		)

		XCTAssertEqual(expectedGame, scores)
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

		let scoresStream = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
			$0.frames = .liveValue
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "HP", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 15),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "L", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "2", didFoul: false)], score: 30),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "R", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 60),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "13", didFoul: false), .init(index: 2, displayValue: "2", didFoul: false)], score: 90),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "L", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "13", didFoul: false)], score: 118),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "R", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 131),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "A", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 142),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 162),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 182),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 192),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreExampleGame2() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
			$0.frames = .liveValue
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(1))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(1),
			index: 1,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "H2", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "3", didFoul: false)], score: 15),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "HS", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "2", didFoul: false)], score: 30),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 75),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "10", didFoul: false)], score: 115),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "10", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 140),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 150),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "12", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "12", didFoul: false)], score: 177),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "12", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 189),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 234),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "X", didFoul: false), .init(index: 2, displayValue: "HP", didFoul: false)], score: 269),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreExampleGame3() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
			$0.frames = .liveValue
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(2))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(2),
			index: 2,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: true), .init(index: 1, displayValue: "15", didFoul: false), .init(index: 2, displayValue: "13", didFoul: false)], score: 28),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "X", didFoul: true), .init(index: 1, displayValue: "13", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 41),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "L", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 54),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "L", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "13", didFoul: false)], score: 82),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "R", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 95),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "R", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 115),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 125),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "-", didFoul: false)], score: 135),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 150),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "5", didFoul: false), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 165),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreExampleGame4() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
			$0.frames = .liveValue
			$0.scores = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(3))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(3),
			index: 3,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "A", didFoul: true), .init(index: 1, displayValue: "2", didFoul: false), .init(index: 2, displayValue: "2", didFoul: false)], score: 0),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "HS", didFoul: true), .init(index: 1, displayValue: "5", didFoul: false), .init(index: 2, displayValue: "2", didFoul: false)], score: 0),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "12", didFoul: true), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "12", didFoul: false)], score: 12),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "12", didFoul: true), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "10", didFoul: false)], score: 22),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 37),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "10", didFoul: false)], score: 62),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false), .init(index: 1, displayValue: "-", didFoul: false), .init(index: 2, displayValue: "5", didFoul: false)], score: 77),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "HS", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "11", didFoul: false)], score: 103),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "A", didFoul: false), .init(index: 1, displayValue: "/", didFoul: false), .init(index: 2, displayValue: "15", didFoul: false)], score: 133),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false), .init(index: 1, displayValue: "HS", didFoul: false), .init(index: 2, displayValue: "/", didFoul: false)], score: 163),
			]
		)

		XCTAssertEqual(expectedGame, scores)

	}

	private func buildGame(withId id: Game.ID) -> Game.Edit {
		.init(
			id: id,
			index: 0,
			score: 255,
			locked: .locked,
			scoringMethod: .byFrame,
			excludeFromStatistics: .include,
			matchPlay: nil,
			gear: [],
			lanes: [],
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123),
				preBowl: .regular,
				excludeFromStatistics: .include,
				alley: nil
			)
		)
	}
}
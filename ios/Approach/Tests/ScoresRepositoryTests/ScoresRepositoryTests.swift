import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FramesRepository
@testable import FramesRepositoryInterface
import GamesRepository
@testable import GamesRepositoryInterface
@testable import ModelsLibrary
@testable import ScoreKeeperLibrary
@testable import ScoresRepository
@testable import ScoresRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

final class ScoresRepositoryTests: XCTestCase {
	@Dependency(ScoresRepository.self) var scores

	func testCalculatesScoreForFramesWithSteps() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: false),
				.init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false),
				.init(pinsDowned: [.headPin], didFoul: false),
			],
			[
				.init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin], didFoul: false),
				.init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin], didFoul: false),
				.init(pinsDowned: [.rightTwoPin], didFoul: false),
			],
			[
				.init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: true),
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
			],
			[
				.init(pinsDowned: [.headPin], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
				.init(pinsDowned: [.headPin], didFoul: false),
			],
		]

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: true)], score: 25),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: false)], score: 40),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 70),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "13", didFoul: false, isSecondary: true)], score: 113),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "13", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "2", didFoul: false, isSecondary: true)], score: 143),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "R", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: true)], score: 163),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "5", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 153),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "HP", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 158),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 203),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "HP", didFoul: false, isSecondary: false)], score: 238),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScore_WithSkippedRolls() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: [.leftThreePin, .headPin, .rightTwoPin, .rightThreePin], didFoul: false),
			],
			[
				.init(pinsDowned: [.headPin], didFoul: false),
			],
			[],
			[],
			[],
			[],
			[],
			[],
			[],
		]

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "13", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 28),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "L", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 41),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "HP", didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: 46),
				.init(index: 3, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 4, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 5, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 6, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 7, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 8, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 9, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreWithInvalidRollsAfterStrike() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
			],
			[
				.init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
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

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "10", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 25),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "10", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 35),
				.init(index: 2, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 3, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 4, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 5, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 6, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 7, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 8, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 9, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreWithEmptyFrames() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
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
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
		]

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 15),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: 30),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScore_WithIncompleteTenthFrame_Correctly() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[],
			[],
			[],
			[],
			[],
			[
				.init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin], didFoul: false)
			],
		]

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 0),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 45),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 75),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 90),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: 100),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScore_WithIncompleteTenthFrameWithStrike_Correctly() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
				.init(pinsDowned: [], didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[],
			[],
			[],
			[],
			[],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
				.init(pinsDowned: [.headPin], didFoul: false),
			],
		]

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 0),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 45),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 75),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 90),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 90),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "HP", didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: 110),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreWithFoulsAppliedCorrectly() async throws {
		let (frames, framesContinuation) = AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>.makeStream()

		let scoresStream = withDependencies {
			$0[GamesRepository.self].findIndex = { @Sendable id in .init(id: id, index: 0) }
			$0[FramesRepository.self].observeRolls = { @Sendable _ in frames }
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		let rolls: [[ScoreKeeper.Roll]] = [
			[
				.init(pinsDowned: [], didFoul: true),
				.init(pinsDowned: [], didFoul: true),
				.init(pinsDowned: [], didFoul: true),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: true),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[
				.init(pinsDowned: Pin.fullDeck, didFoul: false),
			],
			[],
			[],
			[],
			[],
			[],
			[],
		]

		framesContinuation.yield(rolls)

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "-", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: true, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: true, isSecondary: false)], score: 0),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "X", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 0),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: 30),
				.init(index: 4, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 5, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 6, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 7, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 8, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
				.init(index: 9, rolls: [.init(index: 0, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 1, displayValue: nil, didFoul: false, isSecondary: false), .init(index: 2, displayValue: nil, didFoul: false, isSecondary: false)], score: nil),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testScoresStrikeCorrectly() {
		// FIXME: testScoresStrikeCorrectly
		XCTExpectFailure("Not implemented")
		XCTFail("TODO")
	}

	func testScoresSpareCorrectly() {
		// FIXME: testScoresSpareCorrectly
		XCTExpectFailure("Not implemented")
		XCTFail("TODO")
	}

	func testScoresStrikeInLastFrameCorrectly() {
		// FIXME: testScoresStrikeInLastFrameCorrectly
		XCTExpectFailure("Not implemented")
		XCTFail("TODO")
	}

	func testScoresSpareInLastFrameCorrectly() {
		// FIXME: testScoresSpareInLastFrameCorrectly
		XCTExpectFailure("Not implemented")
		XCTFail("TODO")
	}

	func testScoresOpenFrameCorrectly() {
		// FIXME: testScoresOpenFrameCorrectly
		XCTExpectFailure("Not implemented")
		XCTFail("TODO")
	}

	func testScoreCannotGoNegative() {
		// FIXME: testScoreCannotGoNegative
		XCTExpectFailure("Not implemented")
		XCTFail("TODO")
	}

	func testCalculatesScoreExampleGame1() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GamesRepository.self] = .liveValue
			$0[FramesRepository.self] = .liveValue
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(0))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(0),
			index: 0,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "HP", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "L", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "2", didFoul: false, isSecondary: false)], score: 30),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "R", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 60),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "13", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "2", didFoul: false, isSecondary: true)], score: 90),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "L", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "13", didFoul: false, isSecondary: true)], score: 118),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "R", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 131),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "A", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 142),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: true)], score: 162),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: true)], score: 182),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 192),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreExampleGame2() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GamesRepository.self] = .liveValue
			$0[FramesRepository.self] = .liveValue
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(1))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(1),
			index: 1,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "H2", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "3", didFoul: false, isSecondary: false)], score: 15),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "HS", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "2", didFoul: false, isSecondary: false)], score: 30),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 75),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "10", didFoul: false, isSecondary: true)], score: 115),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "10", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 140),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 150),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "12", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "12", didFoul: false, isSecondary: true)], score: 177),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "12", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 189),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 234),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "HP", didFoul: false, isSecondary: false)], score: 269),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreExampleGame3() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GamesRepository.self] = .liveValue
			$0[FramesRepository.self] = .liveValue
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(2))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(2),
			index: 2,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "X", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "15", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "13", didFoul: false, isSecondary: true)], score: 28),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "X", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "13", didFoul: false, isSecondary: true), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: true)], score: 41),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "L", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 54),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "L", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "13", didFoul: false, isSecondary: true)], score: 82),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "R", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 95),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "R", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: true)], score: 115),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 125),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "-", didFoul: false, isSecondary: false)], score: 135),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: false)], score: 150),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: false)], score: 165),
			]
		)

		XCTAssertEqual(expectedGame, scores)
	}

	func testCalculatesScoreExampleGame4() async throws {
		let db = try generatePopulatedDatabase()

		let scoresStream = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GamesRepository.self] = .liveValue
			$0[FramesRepository.self] = .liveValue
			$0[ScoresRepository.self] = .liveValue
		} operation: {
			self.scores.observeScore(for: UUID(3))
		}

		var iterator = scoresStream.makeAsyncIterator()
		let scores = try await iterator.next()

		let expectedGame = ScoredGame(
			id: UUID(3),
			index: 3,
			frames: [
				.init(index: 0, rolls: [.init(index: 0, displayValue: "A", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "2", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "2", didFoul: false, isSecondary: false)], score: 0),
				.init(index: 1, rolls: [.init(index: 0, displayValue: "HS", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "5", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "2", didFoul: false, isSecondary: false)], score: 0),
				.init(index: 2, rolls: [.init(index: 0, displayValue: "12", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "12", didFoul: false, isSecondary: true)], score: 12),
				.init(index: 3, rolls: [.init(index: 0, displayValue: "12", didFoul: true, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "10", didFoul: false, isSecondary: true)], score: 22),
				.init(index: 4, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: false)], score: 37),
				.init(index: 5, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "10", didFoul: false, isSecondary: true)], score: 62),
				.init(index: 6, rolls: [.init(index: 0, displayValue: "C/O", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "-", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "5", didFoul: false, isSecondary: false)], score: 77),
				.init(index: 7, rolls: [.init(index: 0, displayValue: "HS", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "11", didFoul: false, isSecondary: true)], score: 103),
				.init(index: 8, rolls: [.init(index: 0, displayValue: "A", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "/", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "15", didFoul: false, isSecondary: true)], score: 133),
				.init(index: 9, rolls: [.init(index: 0, displayValue: "X", didFoul: false, isSecondary: false), .init(index: 1, displayValue: "HS", didFoul: false, isSecondary: false), .init(index: 2, displayValue: "/", didFoul: false, isSecondary: false)], score: 163),
			]
		)

		XCTAssertEqual(expectedGame, scores)

	}
}

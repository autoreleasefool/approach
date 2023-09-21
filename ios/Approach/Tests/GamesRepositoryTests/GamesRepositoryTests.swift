import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
@testable import GamesRepository
@testable import GamesRepositoryInterface
import GRDB
@testable import MatchPlaysRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class GamesRepositoryTests: XCTestCase {
	@Dependency(\.games) var games

	// MARK: List

	func testList_ReturnsAllGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.seriesGames(forId: UUID(0), ordering: .byIndex)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), bowlerId: UUID(0), index: 0, score: 0),
			.init(id: UUID(1), bowlerId: UUID(0), index: 1, score: 0),
		])
	}

	func testList_FilterBySeries_ReturnsSeriesGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Fetching the games by series
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.seriesGames(forId: UUID(0), ordering: .byIndex)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one game
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), bowlerId: UUID(0), index: 0, score: 0),
		])
	}

	func testList_SortsByIndex() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 1)
		let game2 = Game.Database.mock(id: UUID(1), index: 0)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.seriesGames(forId: UUID(0), ordering: .byIndex)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games sorted by index
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), bowlerId: UUID(0), index: 0, score: 0),
			.init(id: UUID(0), bowlerId: UUID(0), index: 1, score: 0),
		])
	}

	// MARK: List Summaries

	func testSummariesList_ReturnsAllGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.seriesGamesSummaries(forId: UUID(0), ordering: .byIndex)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), index: 0, score: 0),
			.init(id: UUID(1), index: 1, score: 0),
		])
	}

	func testSummariesList_FilterBySeries_ReturnsSeriesGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Fetching the games by series
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.seriesGamesSummaries(forId: UUID(0), ordering: .byIndex)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one game
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), index: 0, score: 0),
		])
	}

	func testSummariesList_SortsByIndex() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 1)
		let game2 = Game.Database.mock(id: UUID(1), index: 0)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.seriesGamesSummaries(forId: UUID(0), ordering: .byIndex)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games sorted by index
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), index: 0, score: 0),
			.init(id: UUID(0), index: 1, score: 0),
		])
	}

	// MARK: Matches Against Opponent

	func testMatchesAgainstOpponent_ReturnsGamesAgainstOpponent() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let game3 = Game.Database.mock(id: UUID(2), index: 2)
		let matchPlay1 = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), opponentId: UUID(0), opponentScore: 1, result: .won)
		let matchPlay2 = MatchPlay.Database.mock(gameId: UUID(1), id: UUID(1), opponentId: UUID(1), opponentScore: 2, result: .lost)
		let db = try initializeDatabase(
			withGames: .custom([game1, game2, game3]),
			withMatchPlays: .custom([matchPlay1, matchPlay2])
		)

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.matches(against: UUID(0))
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games
		XCTAssertEqual(fetched, [.init(id: UUID(0), score: 0, opponentScore: 1, result: .won)])
	}

	func testMatchesAgainstOpponent_OrdersByMostRecent() async throws {
		// Given a database with two games
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 2))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 1))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 3))
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 2)
		let matchPlay1 = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), opponentId: UUID(0), opponentScore: 1, result: .won)
		let matchPlay2 = MatchPlay.Database.mock(gameId: UUID(1), id: UUID(1), opponentId: UUID(0), opponentScore: 2, result: .lost)
		let matchPlay3 = MatchPlay.Database.mock(gameId: UUID(2), id: UUID(2), opponentId: UUID(0), opponentScore: 3, result: .tied)
		let db = try initializeDatabase(
			withSeries: .custom([series1, series2, series3]),
			withGames: .custom([game1, game2, game3]),
			withMatchPlays: .custom([matchPlay1, matchPlay2, matchPlay3])
		)

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.matches(against: UUID(0))
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games
		XCTAssertEqual(fetched, [
			.init(id: UUID(2), score: 0, opponentScore: 3, result: .tied),
			.init(id: UUID(0), score: 0, opponentScore: 1, result: .won),
			.init(id: UUID(1), score: 0, opponentScore: 2, result: .lost),
		])
	}

	// MARK: Share Games

	func testShareGames_WhenGamesExist_ReturnsGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, score: 234, scoringMethod: .manual)
		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: "000100", roll1: "111000", roll2: "000011", ball1: UUID(0))
		let frame2 = Frame.Database.mock(gameId: UUID(0), index: 1, roll0: "001111", roll1: "000000", roll2: "010000", ball0: UUID(0))
		let frame3 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: "000100", roll1: "011000", roll2: "000011", ball0: UUID(0))
		let db = try initializeDatabase(withGames: .custom([game1, game2]), withGameLanes: .zero, withGameGear: .zero, withFrames: .custom([frame1, frame2, frame3]))

		// Fetching the games
		let games = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.shareGames([UUID(0), UUID(1)])
		}

		// Returns all the games
		XCTAssertEqual(games, [
			.init(
				id: UUID(0),
				index: 0,
				score: 123,
				scoringMethod: .byFrame,
				frames: [
					.init(
						gameId: UUID(0),
						index: 0,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: true), bowlingBall: .init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))),
							.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false), bowlingBall: nil),
						]
					),
					.init(
						gameId: UUID(0),
						index: 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin], didFoul: false), bowlingBall: .init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))),
							.init(index: 1, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin], didFoul: false), bowlingBall: nil),
						]
					),
				],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors"),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					alley: .init(name: "Skyview")
				)
			),
			.init(
				id: UUID(1),
				index: 1,
				score: 234,
				scoringMethod: .manual,
				frames: [
					.init(
						gameId: UUID(1),
						index: 0,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: .init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: false), bowlingBall: nil),
							.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false), bowlingBall: nil),
						]
					),
				],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors"),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					alley: .init(name: "Skyview")
				)
			),
		])
	}

	func testShareGames_WhenGamesNotExist_ThrowsError() async throws {
		// Given a database with one game
		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Fetching the games throws an error
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0.database.reader = { db }
				$0.games = .liveValue
			} operation: {
				_ = try await self.games.shareGames([UUID(0), UUID(1)])
			}
		}
	}

	// MARK: Share Series

	func testShareSeries_WhenSeriesExists_ReturnsGames() async throws {
		// Given a database with a series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 1234))
		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1)
		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: "000100", roll1: "111000", roll2: "000011", ball1: UUID(0))
		let frame2 = Frame.Database.mock(gameId: UUID(0), index: 1, roll0: "001111", roll1: "000000", roll2: "010000", ball0: UUID(0))
		let frame3 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: "000100", roll1: "011000", roll2: "000011", ball0: UUID(0))
		let db = try initializeDatabase(withSeries: .custom([series1, series2]), withGames: .custom([game1, game2]), withGameLanes: .zero, withGameGear: .zero, withFrames: .custom([frame1, frame2, frame3]))

		// Fetching the games
		let games = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.shareSeries(UUID(0))
		}

		// Returns all the games
		XCTAssertEqual(games, [
			.init(
				id: UUID(0),
				index: 0,
				score: 123,
				scoringMethod: .byFrame,
				frames: [
					.init(
						gameId: UUID(0),
						index: 0,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: true), bowlingBall: .init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))),
							.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin], didFoul: false), bowlingBall: nil),
						]
					),
					.init(
						gameId: UUID(0),
						index: 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin], didFoul: false), bowlingBall: .init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))),
							.init(index: 1, roll: .init(pinsDowned: [], didFoul: false), bowlingBall: nil),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin], didFoul: false), bowlingBall: nil),
						]
					),
				],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors"),
				series: .init(date: Date(timeIntervalSince1970: 123), alley: nil)
			),
		])
	}

	func testShareSeries_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no series
		let db = try initializeDatabase(withSeries: .zero)

		// Fetching the series throws an error
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0.database.reader = { db }
				$0.games = .liveValue
			} operation: {
				_ = try await self.games.shareSeries(UUID(0))
			}
		}
	}

	// MARK: Observe

	func testObserve_WhenGameExists_ReturnsGame() async throws {
		// Given a database with one game
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Editing the game
		let game = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.observe(UUID(0))
		}

		var iterator = game.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the game
		XCTAssertEqual(
			fetched,
			.init(
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include,
				matchPlay: nil,
				gear: [],
				lanes: [],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					alley: .init(id: UUID(0), name: "Skyview")
				)
			)
		)
	}

	func testEdit_WhenGameHasMatchPlay_ReturnsGameWithMatchPlay() async throws {
		// Given a database with one game and match play
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let matchPlay1 = MatchPlay.Database(
			gameId: UUID(0),
			id: UUID(0),
			opponentId: UUID(0),
			opponentScore: 123,
			result: .lost
		)
		let db = try initializeDatabase(withGames: .custom([game1]), withMatchPlays: .custom([matchPlay1]))

		// Editing the game
		let game = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.observe(UUID(0))
		}

		var iterator = game.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the game
		XCTAssertEqual(
			fetched,
			.init(
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include,
				matchPlay: .init(
					gameId: UUID(0),
					id: UUID(0),
					opponent: .init(id: UUID(0), name: "Joseph"),
					opponentScore: 123,
					result: .lost
				),
				gear: [],
				lanes: [],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					alley: .init(id: UUID(0), name: "Skyview")
				)
			)
		)
	}

	func testEdit_WhenGameHasGear_ReturnsGameWithGear() async throws {
		// Given a database with one game and 3 gear
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Towel", kind: .towel)
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Shoes", kind: .shoes)
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Other", kind: .other)
		let gameGear1 = GameGear.Database(gameId: UUID(0), gearId: UUID(0))
		let gameGear2 = GameGear.Database(gameId: UUID(0), gearId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2, gear3]), withGames: .custom([game1]), withGameGear: .custom([gameGear1, gameGear2]), withBowlerPreferredGear: .zero)

		// Editing the game
		let game = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.observe(UUID(0))
		}

		var iterator = game.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the game
		XCTAssertEqual(
			fetched,
			.init(
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include,
				matchPlay: nil,
				gear: [
					.init(id: UUID(1), name: "Shoes", kind: .shoes, ownerName: nil, avatar: .mock(id: UUID(0))),
					.init(id: UUID(0), name: "Towel", kind: .towel, ownerName: nil, avatar: .mock(id: UUID(0))),
				],
				lanes: [],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					alley: .init(id: UUID(0), name: "Skyview")
				)
			)
		)
	}

	func testEdit_WhenGameHasLanes_ReturnsGameWithLanes() async throws {
		// Given a database with one game and 3 gear
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "10", position: .noWall)
		let lane3 = Lane.Database(alleyId: UUID(0), id: UUID(2), label: "2", position: .rightWall)
		let lane4 = Lane.Database(alleyId: UUID(0), id: UUID(3), label: "3", position: .noWall)
		let gameLane1 = GameLane.Database(gameId: UUID(0), laneId: UUID(0))
		let gameLane2 = GameLane.Database(gameId: UUID(0), laneId: UUID(1))
		let gameLane3 = GameLane.Database(gameId: UUID(0), laneId: UUID(2))
		let db = try initializeDatabase(withLanes: .custom([lane1, lane2, lane3, lane4]), withGames: .custom([game1]), withGameLanes: .custom([gameLane1, gameLane2, gameLane3]))

		// Editing the game
		let game = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.observe(UUID(0))
		}

		var iterator = game.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the game
		XCTAssertEqual(
			fetched,
			.init(
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include,
				matchPlay: nil,
				gear: [],
				lanes: [
					.init(id: UUID(0), label: "1"),
					.init(id: UUID(2), label: "2"),
					.init(id: UUID(1), label: "10"),
				],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					alley: .init(id: UUID(0), name: "Skyview")
				)
			)
		)
	}

	func testEdit_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no games
		let db = try initializeDatabase(withGames: nil)

		// Editing the game
		let game = withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			self.games.observe(UUID(0))
		}

		var iterator = game.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns nil
		XCTAssertNil(fetched??.id)
	}

	// MARK: - Find Index

	func testFindIndex_WhenGameExists_ReturnsIndex() async throws {
		// Given a database with 1 game
		let game1 = Game.Database.mock(id: UUID(0), index: 23)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Fetching the game
		let game = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.findIndex(UUID(0))
		}

		XCTAssertEqual(game, .init(id: UUID(0), index: 23))
	}

	func testFindIndex_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no games
		let db = try initializeDatabase(withGames: nil)

		// Fetching the game
		let game = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.findIndex(UUID(0))
		}

		// Returns nil
		XCTAssertNil(game)
	}

	// MARK: Update

	func testUpdate_WhenGameExists_UpdatesGame() async throws {
		// Given a database with a game
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Editing the game
		let editable = Game.Edit(
			id: UUID(0),
			index: 0,
			score: 123,
			locked: .locked,
			scoringMethod: .manual,
			excludeFromStatistics: .include,
			matchPlay: nil,
			gear: [],
			lanes: [],
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
				preBowl: .regular,
				excludeFromStatistics: .include,
				alley: .init(id: UUID(0), name: "Skyview")
			)
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Game.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.index, 0)
		XCTAssertEqual(updated?.score, 123)
		XCTAssertEqual(updated?.locked, .locked)
		XCTAssertEqual(updated?.scoringMethod, .manual)

		// Does not insert any records
		let count = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenHasMatchPlay_UpdatesMatchPlay() async throws {
		// Given a database with a game and a match play
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let matchPlay1 = MatchPlay.Database(gameId: UUID(0), id: UUID(0), opponentId: UUID(0), opponentScore: 123, result: nil)
		let db = try initializeDatabase(withGames: .custom([game1]), withMatchPlays: .custom([matchPlay1]))

		// Editing the game
		let editable = Game.Edit(
			id: UUID(0),
			index: 0,
			score: 0,
			locked: .open,
			scoringMethod: .byFrame,
			excludeFromStatistics: .include,
			matchPlay: .init(
				gameId: UUID(0),
				id: UUID(0),
				opponent: .init(id: UUID(1), name: "Sarah"),
				opponentScore: 456,
				result: .lost
			),
			gear: [],
			lanes: [],
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
				preBowl: .regular,
				excludeFromStatistics: .include,
				alley: .init(id: UUID(0), name: "Skyview")
			)
		)

		let updatedMatchPlay = self.expectation(description: "updated match play")
		try await withDependencies {
			$0.database.writer = { db }
			$0.games.update = GamesRepository.liveValue.update
			$0.matchPlays.update = { matchPlay in
				XCTAssertEqual(
					matchPlay,
					.init(
						gameId: UUID(0),
						id: UUID(0),
						opponent: .init(id: UUID(1), name: "Sarah"),
						opponentScore: 456,
						result: .lost
					)
				)
				updatedMatchPlay.fulfill()
			}
		} operation: {
			try await self.games.update(editable)
		}

		await fulfillment(of: [updatedMatchPlay])
	}

	func testUpdate_WhenHasGear_UpdatesGear() async throws {
		// Given a database with a game and gear
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Towel", kind: .towel)
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Shoes", kind: .shoes)
		let gameGear1 = GameGear.Database(gameId: UUID(0), gearId: UUID(0))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2]), withGames: .custom([game1]), withGameGear: .custom([gameGear1]), withBowlerPreferredGear: .zero)

		// Editing the game with a different gear
		let editable = Game.Edit(
			id: UUID(0),
			index: 0,
			score: 0,
			locked: .open,
			scoringMethod: .byFrame,
			excludeFromStatistics: .include,
			matchPlay: nil,
			gear: [
				.init(id: UUID(1), name: "Shoes", kind: .shoes, ownerName: nil, avatar: .mock(id: UUID(0))),
			],
			lanes: [],
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
				preBowl: .regular,
				excludeFromStatistics: .include,
				alley: .init(id: UUID(0), name: "Skyview")
			)
		)

		try await withDependencies {
			$0.database.writer = { db }
			$0.games.update = GamesRepository.liveValue.update
		} operation: {
			try await self.games.update(editable)
		}

		// It deletes the old GameGear and creates the new association
		let gameGear = try await db.read { try GameGear.Database.fetchAll($0 ) }
		XCTAssertEqual(gameGear, [.init(gameId: UUID(0), gearId: UUID(1))])
	}

	func testUpdate_WhenHasLanes_UpdatesLanes() async throws {
		// Given a database with a game and lanes
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .noWall)
		let gameLane1 = GameLane.Database(gameId: UUID(0), laneId: UUID(0))
		let db = try initializeDatabase(withLanes: .custom([lane1, lane2]), withGames: .custom([game1]), withGameLanes: .custom([gameLane1]))

		// Editing the game with a different gear
		let editable = Game.Edit(
			id: UUID(0),
			index: 0,
			score: 0,
			locked: .open,
			scoringMethod: .byFrame,
			excludeFromStatistics: .include,
			matchPlay: nil,
			gear: [],
			lanes: [
				.init(id: UUID(1), label: "2"),
			],
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
				preBowl: .regular,
				excludeFromStatistics: .include,
				alley: .init(id: UUID(0), name: "Skyview")
			)
		)

		try await withDependencies {
			$0.database.writer = { db }
			$0.games.update = GamesRepository.liveValue.update
		} operation: {
			try await self.games.update(editable)
		}

		// It deletes the old GameLane and creates the new association
		let gameLane = try await db.read { try GameLane.Database.fetchAll($0 ) }
		XCTAssertEqual(gameLane, [.init(gameId: UUID(0), laneId: UUID(1))])
	}

	func testUpdate_WhenGameNotExists_ThrowError() async throws {
		// Given a database with no games
		let db = try initializeDatabase(withGames: nil)

		// Updating a game
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Game.Edit(
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .locked,
				scoringMethod: .byFrame,
				excludeFromStatistics: .exclude,
				matchPlay: nil,
				gear: [],
				lanes: [],
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					alley: .init(id: UUID(0), name: "Skyview")
				)
			)
			try await withDependencies {
				$0.database.writer = { db }
				$0.games = .liveValue
			} operation: {
				try await self.games.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: Delete

	func testDelete_WhenIdExists_DeletesGame() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let db = try initializeDatabase(withGames: .custom([game1, game2]))

		// Deleting the first game
		try await withDependencies {
			$0.database.writer = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try Game.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other game intact
		let otherExists = try await db.read { try Game.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with one game
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Deleting a non-existent series
		try await withDependencies {
			$0.database.writer = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.delete(UUID(1))
		}

		// Leaves the game
		let exists = try await db.read { try Game.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}

	// MARK: - Duplicate Lanes

	func testDuplicateLanes_WhenGameHasNoLanes_DoesNothing() async throws {
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let game3 = Game.Database.mock(id: UUID(2), index: 2)
		let db = try initializeDatabase(withLanes: .default, withGames: .custom([game1, game2, game3]), withGameLanes: .zero)

		try await withDependencies {
			$0.database.writer = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.duplicateLanes(from: UUID(0), toAllGames: [UUID(1), UUID(2)])
		}

		let gameLanes = try await db.read { try GameLane.Database.fetchCount($0) }
		XCTAssertEqual(gameLanes, 0)
	}

	func testDuplicateLanes_WhenGameHasLanes_CopiesToAllOtherGames() async throws {
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let game3 = Game.Database.mock(id: UUID(2), index: 2)
		let gameLane1 = GameLane.Database(gameId: UUID(0), laneId: UUID(0))
		let gameLane2 = GameLane.Database(gameId: UUID(0), laneId: UUID(1))
		let db = try initializeDatabase(withLanes: .default, withGames: .custom([game1, game2, game3]), withGameLanes: .custom([gameLane1, gameLane2]))

		try await withDependencies {
			$0.database.writer = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.duplicateLanes(from: UUID(0), toAllGames: [UUID(1), UUID(2)])
		}

		let gameLanes = try await db.read {
			try GameLane.Database
				.order(GameLane.Database.Columns.gameId, GameLane.Database.Columns.laneId)
				.fetchAll($0)
		}

		XCTAssertEqual(gameLanes, [
			.init(gameId: UUID(0), laneId: UUID(0)),
			.init(gameId: UUID(0), laneId: UUID(1)),
			.init(gameId: UUID(1), laneId: UUID(0)),
			.init(gameId: UUID(1), laneId: UUID(1)),
			.init(gameId: UUID(2), laneId: UUID(0)),
			.init(gameId: UUID(2), laneId: UUID(1)),
		])
	}

	func testDuplicateLanes_WhenOtherGameHasLanes_Appends() async throws {
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let game3 = Game.Database.mock(id: UUID(2), index: 2)
		let gameLane1 = GameLane.Database(gameId: UUID(0), laneId: UUID(0))
		let gameLane2 = GameLane.Database(gameId: UUID(0), laneId: UUID(1))
		let gameLane3 = GameLane.Database(gameId: UUID(1), laneId: UUID(1))
		let gameLane4 = GameLane.Database(gameId: UUID(2), laneId: UUID(1))
		let db = try initializeDatabase(withLanes: .default, withGames: .custom([game1, game2, game3]), withGameLanes: .custom([gameLane1, gameLane2, gameLane3, gameLane4]))

		try await withDependencies {
			$0.database.writer = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.duplicateLanes(from: UUID(0), toAllGames: [UUID(1), UUID(2)])
		}

		let gameLanes = try await db.read {
			try GameLane.Database
				.order(GameLane.Database.Columns.gameId, GameLane.Database.Columns.laneId)
				.fetchAll($0)
		}

		XCTAssertEqual(gameLanes, [
			.init(gameId: UUID(0), laneId: UUID(0)),
			.init(gameId: UUID(0), laneId: UUID(1)),
			.init(gameId: UUID(1), laneId: UUID(0)),
			.init(gameId: UUID(1), laneId: UUID(1)),
			.init(gameId: UUID(2), laneId: UUID(0)),
			.init(gameId: UUID(2), laneId: UUID(1)),
		])
	}
}

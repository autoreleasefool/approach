import DatabaseModelsLibrary
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
		XCTAssertEqual(fetched, [.init(game1), .init(game2)])
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
		XCTAssertEqual(fetched, [.init(game1)])
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
		XCTAssertEqual(fetched, [.init(game2), .init(game1)])
	}

	// MARK: Edit

	func testEdit_WhenGameExists_ReturnsGame() async throws {
		// Given a database with one game
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Editing the game
		let game = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.edit(UUID(0))
		}

		// Returns the game
		XCTAssertEqual(
			game,
			.init(
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include,
				matchPlay: nil,
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					excludeFromStatistics: .include,
					alley: .init(name: "Skyview"),
					lanes: [
						.init(id: UUID(0), label: "1"),
						.init(id: UUID(1), label: "2"),
					]
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
		let game = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.edit(UUID(0))
		}

		// Returns the game
		XCTAssertEqual(
			game,
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
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					excludeFromStatistics: .include,
					alley: .init(name: "Skyview"),
					lanes: [
						.init(id: UUID(0), label: "1"),
						.init(id: UUID(1), label: "2"),
					]
				)
			)
		)
	}

	func testEdit_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no games
		let db = try initializeDatabase(withGames: nil)

		// Editing the game
		let game = try await withDependencies {
			$0.database.reader = { db }
			$0.games = .liveValue
		} operation: {
			try await self.games.edit(UUID(0))
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
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
				excludeFromStatistics: .include,
				alley: .init(name: "Skyview"),
				lanes: []
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
			bowler: .init(name: "Joseph"),
			league: .init(name: "Majors", excludeFromStatistics: .include),
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
				excludeFromStatistics: .include,
				alley: .init(name: "Skyview"),
				lanes: []
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
				bowler: .init(name: "Joseph"),
				league: .init(name: "Majors", excludeFromStatistics: .include),
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
					excludeFromStatistics: .include,
					alley: .init(name: "Skyview"),
					lanes: []
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
}

extension Game.Database {
	static func mock(
		seriesId: Series.ID = UUID(0),
		id: ID,
		index: Int,
		score: Int = 0,
		locked: Game.Lock = .open,
		scoringMethod: Game.ScoringMethod = .byFrame,
		excludeFromStatistics: Game.ExcludeFromStatistics = .include
	) -> Self {
		.init(
			seriesId: seriesId,
			id: id,
			index: index,
			score: score,
			locked: locked,
			scoringMethod: scoringMethod,
			excludeFromStatistics: excludeFromStatistics
		)
	}
}

extension Game.Summary {
	init(_ from: Game.Database) {
		self.init(
			id: from.id,
			index: from.index,
			score: from.score,
			scoringMethod: from.scoringMethod
		)
	}
}

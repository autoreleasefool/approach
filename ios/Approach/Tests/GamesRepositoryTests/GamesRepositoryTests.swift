import DatabaseModelsLibrary
@testable import DatabaseService
import Dependencies
@testable import GamesRepository
@testable import GamesRepositoryInterface
import GRDB
import ModelsLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class GamesRepositoryTests: XCTestCase {
	let bowlerId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000001A")!
	let leagueId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000001B")!

	let seriesId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!
	let seriesId2 = UUID(uuidString: "00000000-0000-0000-0000-00000000000B")!

	let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
	let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

	func testList_ReturnsAllGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: id1, ordinal: 1)
		let game2 = Game.Database.mock(id: id2, ordinal: 2)
		let db = try await initializeDatabase(inserting: [game1, game2])

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
		} operation: {
			GamesRepository.liveValue.seriesGames(forId: seriesId1, ordering: .byOrdinal)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games
		XCTAssertEqual(fetched, [.init(game1), .init(game2)])
	}

	func testList_FilterBySeries_ReturnsSeriesGames() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(seriesId: seriesId1, id: id1, ordinal: 1)
		let game2 = Game.Database.mock(seriesId: seriesId2, id: id2, ordinal: 2)
		let db = try await initializeDatabase(inserting: [game1, game2])

		// Fetching the games by series
		let games = withDependencies {
			$0.database.reader = { db }
		} operation: {
			GamesRepository.liveValue.seriesGames(forId: seriesId1, ordering: .byOrdinal)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one game
		XCTAssertEqual(fetched, [.init(game1)])
	}

	func testList_SortsByOrdinal() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: id1, ordinal: 2)
		let game2 = Game.Database.mock(id: id2, ordinal: 1)
		let db = try await initializeDatabase(inserting: [game1, game2])

		// Fetching the games
		let games = withDependencies {
			$0.database.reader = { db }
		} operation: {
			GamesRepository.liveValue.seriesGames(forId: seriesId1, ordering: .byOrdinal)
		}
		var iterator = games.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the games sorted by ordinal
		XCTAssertEqual(fetched, [.init(game2), .init(game1)])
	}

	func testEdit_WhenGameExists_ReturnsGame() async throws {
		// Given a database with one game
		let game1 = Game.Database.mock(id: id1, ordinal: 1)
		let db = try await initializeDatabase(inserting: [game1])

		// Editing the game
		let game = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await GamesRepository.liveValue.edit(id1)
		}

		// Returns the game
		XCTAssertEqual(
			game,
			.init(id: id1, locked: .open, manualScore: nil, excludeFromStatistics: .include)
		)
	}

	func testEdit_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no games
		let db = try await initializeDatabase(inserting: [])

		// Editing the game
		let game = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await GamesRepository.liveValue.edit(id1)
		}

		// Returns nil
		XCTAssertNil(game)
	}

	func testUpdate_WhenGameExists_UpdatesGame() async throws {
		// Given a database with a game
		let game1 = Game.Database.mock(id: id1, ordinal: 1, locked: .open, manualScore: nil)
		let db = try await initializeDatabase(inserting: [game1])

		// Editing the game
		let editable = Game.Edit(id: id1, locked: .locked, manualScore: 123, excludeFromStatistics: .include)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GamesRepository.liveValue.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Game.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.ordinal, 1)
		XCTAssertEqual(updated?.locked, .locked)

		// Does not insert any records
		let count = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenGameNotExists_ThrowError() async throws {
		// Given a database with no games
		let db = try await initializeDatabase(inserting: [])

		// Updating a game
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Game.Edit(id: id1, locked: .locked, manualScore: nil, excludeFromStatistics: .exclude)
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await GamesRepository.liveValue.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testDelete_WhenIdExists_DeletesGame() async throws {
		// Given a database with two games
		let game1 = Game.Database.mock(id: id1, ordinal: 1)
		let game2 = Game.Database.mock(id: id2, ordinal: 2)
		let db = try await initializeDatabase(inserting: [game1, game2])

		// Deleting the first game
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GamesRepository.liveValue.delete(self.id1)
		}

		// Updates the database
		let deletedExists = try await db.read { try Game.Database.exists($0, id: self.id1) }
		XCTAssertFalse(deletedExists)

		// And leaves the other game intact
		let otherExists = try await db.read { try Game.Database.exists($0, id: self.id2) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with one game
		let game1 = Game.Database.mock(id: id1, ordinal: 1)
		let db = try await initializeDatabase(inserting: [game1])

		// Deleting a non-existent series
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GamesRepository.liveValue.delete(self.id2)
		}

		// Leaves the game
		let exists = try await db.read { try Game.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)
	}

	private func initializeDatabase(
		inserting games: [Game.Database] = []
	) async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)

		let bowler = Bowler.Database(id: bowlerId1, name: "Joseph", status: .playable)
		let league = League.Database(
			bowlerId: bowlerId1,
			id: leagueId1,
			name: "Majors",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil,
			excludeFromStatistics: .include,
			alleyId: nil
		)
		let series = [
			Series.Database(
				leagueId: leagueId1,
				id: seriesId1,
				date: Date(),
				numberOfGames: 4,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: nil
			),
			Series.Database(
				leagueId: leagueId1,
				id: seriesId2,
				date: Date(),
				numberOfGames: 4,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: nil
			),
		]

		try await dbQueue.write {
			try bowler.insert($0)
			try league.insert($0)
			for series in series {
				try series.insert($0)
			}
			for game in games {
				try game.insert($0)
			}
		}

		return dbQueue
	}
}

extension Game.Database {
	static func mock(
		seriesId: Series.ID = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!,
		id: ID,
		ordinal: Int,
		locked: Game.Lock = .open,
		manualScore: Int? = nil,
		excludeFromStatistics: Game.ExcludeFromStatistics = .include
	) -> Self {
		.init(
			seriesId: seriesId,
			id: id,
			ordinal: ordinal,
			locked: locked,
			manualScore: manualScore,
			excludeFromStatistics: excludeFromStatistics
		)
	}
}

extension Game.Summary {
	init(_ from: Game.Database) {
		self.init(
			id: from.id,
			ordinal: from.ordinal,
			manualScore: from.manualScore
		)
	}
}

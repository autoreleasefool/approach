import DatabaseModelsLibrary
import Dependencies
@testable import GamesRepository
@testable import GamesRepositoryInterface
import GRDB
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class GamesRepositoryTests: XCTestCase {
	@Dependency(\.games) var games

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
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include,
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
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

	func testUpdate_WhenGameExists_UpdatesGame() async throws {
		// Given a database with a game
		let game1 = Game.Database.mock(id: UUID(0), index: 0, locked: .open, manualScore: nil)
		let db = try initializeDatabase(withGames: .custom([game1]))

		// Editing the game
		let editable = Game.Edit(
			id: UUID(0),
			index: 0,
			locked: .locked,
			manualScore: 123,
			excludeFromStatistics: .include,
			series: .init(
				date: Date(timeIntervalSince1970: 123_456_000),
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
		XCTAssertEqual(updated?.locked, .locked)

		// Does not insert any records
		let count = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenGameNotExists_ThrowError() async throws {
		// Given a database with no games
		let db = try initializeDatabase(withGames: nil)

		// Updating a game
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Game.Edit(
				id: UUID(0),
				index: 0,
				locked: .locked,
				manualScore: nil,
				excludeFromStatistics: .exclude,
				series: .init(
					date: Date(timeIntervalSince1970: 123_456_000),
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
		locked: Game.Lock = .open,
		manualScore: Int? = nil,
		excludeFromStatistics: Game.ExcludeFromStatistics = .include
	) -> Self {
		.init(
			seriesId: seriesId,
			id: id,
			index: index,
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
			index: from.index,
			manualScore: from.manualScore
		)
	}
}

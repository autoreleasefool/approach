@testable import BowlersRepository
@testable import BowlersRepositoryInterface
import DatabaseModelsLibrary
import Dependencies
import GRDB
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class BowlersRepositoryTests: XCTestCase {
	@Dependency(\.bowlers) var bowlers

	// MARK: List

	func testList_ReturnsOnlyPlayable() async throws {
		// Given a database with a bowler and opponent
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", status: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns only the playable bowler
		XCTAssertEqual(fetched, [.init(id: UUID(0), name: "Joseph", average: nil)])
	}

	func testList_SortsByName() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by name
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Audriana", average: nil),
			.init(id: UUID(0), name: "Joseph", average: nil),
		])
	}

	func testList_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byRecentlyUsed)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by recently used ids
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: nil),
			.init(id: UUID(1), name: "Audriana", average: nil),
		])
	}

	func testList_WithGames_CalculatesAverages() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .playable)
		// and 2 games each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 200)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 0, score: 250)
		let game4 = Game.Database.mock(seriesId: UUID(2), id: UUID(3), index: 1, score: 300)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1, bowler2]),
			withGames: .custom([game1, game2, game3, game4])
		)

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with averages
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Audriana", average: 275),
			.init(id: UUID(0), name: "Joseph", average: 150),
		])
	}

	func testList_WhenLeagueExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		// 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date())
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date())
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1]),
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2])
		)

		// Fetching the bowler
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: 100),
		])
	}

	func testList_WhenSeriesExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		// 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date(), excludeFromStatistics: .exclude)
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1]),
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2])
		)

		// Fetching the bowler
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: 100),
		])
	}

	func testList_WhenGameExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		// 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date())
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date())
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200, excludeFromStatistics: .exclude)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1]),
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2])
		)

		// Fetching the bowler
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: 100),
		])
	}

	// MARK: Opponents

	func testOpponents_ReturnsPlayablesAndOpponents() async throws {
		// Given a database with a bowler and an opponent
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .opponent)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.opponents(ordered: .byName)
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns both bowlers
		XCTAssertEqual(fetched, [.init(id: UUID(0), name: "Joseph"), .init(id: UUID(1), name: "Sarah")])
	}

	func testOpponents_SortsByName() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .opponent)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.opponents(ordered: .byName)
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the opponents sorted by name
		XCTAssertEqual(fetched, [.init(id: UUID(1), name: "Audriana"), .init(id: UUID(0), name: "Joseph")])
	}

	func testOpponents_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .opponent)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
			$0.bowlers = .liveValue
		} operation: {
			// with `byRecentlyUsed` ordering
			self.bowlers.opponents(ordered: .byRecentlyUsed)
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the opponents sorted by recently used
		XCTAssertEqual(fetched, [.init(id: UUID(0), name: "Joseph"), .init(id: UUID(1), name: "Audriana")])
	}

	// MARK: Summaries

	func testSummaries_ReturnsMatchingBowlers() async throws {
		// Given a database with 3 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .opponent)
		let bowler3 = Bowler.Database(id: UUID(2), name: "Sarah", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2, bowler3]))

		// Fetching the bowlers
		let bowlers = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.summaries(forIds: [UUID(0), UUID(1)])
		}

		// Returns the expected bowlers
		XCTAssertEqual(bowlers, [.init(id: UUID(0), name: "Joseph"), .init(id: UUID(1), name: "Audriana")])
	}

	func testSummaries_SortsByIDs() async throws {
		// Given a database with 3 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", status: .opponent)
		let bowler3 = Bowler.Database(id: UUID(2), name: "Sarah", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2, bowler3]))

		// Fetching the bowlers
		let bowlers = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			// With a specific ID ordering
			try await self.bowlers.summaries(forIds: [UUID(2), UUID(0), UUID(1)])
		}

		// Returns the bowlers in order
		XCTAssertEqual(
			bowlers,
			[
				.init(id: UUID(2), name: "Sarah"),
				.init(id: UUID(0), name: "Joseph"),
				.init(id: UUID(1), name: "Audriana"),
			]
		)
	}

	// MARK: Create

	func testCreate_WhenBowlerExists_ThrowsError() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Create the bowler
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = Bowler.Create(id: UUID(0), name: "Joe", status: .playable)
			try await withDependencies {
				$0.database.writer = { db }
				$0.bowlers = .liveValue
			} operation: {
				try await self.bowlers.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Joseph")
		XCTAssertEqual(updated?.status, .opponent)
	}

	func testCreate_WhenBowlerNotExists_CreatesBowler() async throws {
		// Given a database with no bowlers
		let db = try initializeDatabase(withBowlers: nil)

		// Creating a bowler
		let create = Bowler.Create(id: UUID(0), name: "Joe", status: .playable)
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.status, .playable)
	}

	// MARK: Update

	func testUpdate_WhenBowlerExists_UpdatesBowler() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Editing the bowler
		let editable = Bowler.Edit(id: UUID(0), name: "Joe")
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.status, .opponent)

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenBowlerNotExists_ThrowError() async throws {
		// Given a database with no bowlers
		let db = try initializeDatabase(withBowlers: nil)

		// Updating a bowler
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Bowler.Edit(id: UUID(0), name: "Joe")
			try await withDependencies {
				$0.database.writer = { db }
				$0.bowlers = .liveValue
			} operation: {
				try await self.bowlers.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: Edit

	func testEdit_WhenBowlerExists_ReturnsBowler() async throws {
		// Given a database with a bowler
		let bowler = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler]))

		// Editing the bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.edit(UUID(0))
		}

		// Returns the bowler
		XCTAssertEqual(editable, .init(id: UUID(0), name: "Joseph"))
	}

	func testEdit_WhenBowlerNotExists_ReturnsNil() async throws {
		// Given a database with no bowlers
		let db = try initializeDatabase(withBowlers: nil)

		// Editing a bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.edit(UUID(0))
		}

		// Returns nil
		XCTAssertNil(editable)
	}

	// MARK: Delete

	func testDelete_WhenIdExists_DeletesBowler() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", status: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Deleting the first bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other bowler intact
		let otherExists = try await db.read { try Bowler.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", status: .playable)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Deleting a non-existent bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.delete(UUID(1))
		}

		// Leaves the bowler
		let exists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}

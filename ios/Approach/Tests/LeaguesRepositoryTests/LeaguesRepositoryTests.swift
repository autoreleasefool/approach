import DatabaseModelsLibrary
import Dependencies
import GRDB
@testable import LeaguesRepository
@testable import LeaguesRepositoryInterface
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class LeaguesRepositoryTests: XCTestCase {
	@Dependency(\.leagues) var leagues

	// MARK: - List

	func testList_ReturnsAllLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		let db = try initializeDatabase(withLeagues: .custom([league1, league2]))

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Majors", average: nil),
			.init(id: UUID(1), name: "Minors", average: nil),
		])
	}

	func testList_FilterByRecurrence_ReturnsMatchingLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", recurrence: .once)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", recurrence: .repeating)
		let db = try initializeDatabase(withLeagues: .custom([league1, league2]))

		// Fetching the leagues by recurrence
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), withRecurrence: .once, ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one league
		XCTAssertEqual(fetched, [.init(id: UUID(0), name: "Majors", average: nil)])
	}

	func testList_FilterByBowler_ReturnsBowlerLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(bowlerId: UUID(0), id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(bowlerId: UUID(1), id: UUID(1), name: "Minors")
		let db = try initializeDatabase(withLeagues: .custom([league1, league2]))

		// Fetching the leagues by bowler
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one league
		XCTAssertEqual(fetched, [.init(id: UUID(0), name: "Majors", average: nil)])
	}

	func testList_SortsByName() async throws {
		// Given a database with three leagues
		let league1 = League.Database.mock(id: UUID(0), name: "B League")
		let league2 = League.Database.mock(id: UUID(1), name: "A League")
		let league3 = League.Database.mock(id: UUID(2), name: "C League")
		let db = try initializeDatabase(withLeagues: .custom([league1, league2, league3]))

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues sorted by name
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "A League", average: nil),
			.init(id: UUID(0), name: "B League", average: nil),
			.init(id: UUID(2), name: "C League", average: nil),
		])
	}

	func testList_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: UUID(0), name: "B League")
		let league2 = League.Database.mock(id: UUID(1), name: "A League")
		let db = try initializeDatabase(withLeagues: .custom([league1, league2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byRecentlyUsed)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues sorted by recently used ids
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "B League", average: nil),
			.init(id: UUID(1), name: "A League", average: nil),
		])
	}

	func testList_WithGames_CalculatesAverages() async throws {
		// Given a database with 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		// and 2 games each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 200)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 0, score: 250)
		let game4 = Game.Database.mock(seriesId: UUID(2), id: UUID(3), index: 1, score: 300)
		let db = try initializeDatabase(
			withLeagues: .custom([league1, league2]),
			withGames: .custom([game1, game2, game3, game4])
		)

		// Fetching the league
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the leagues with averages
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Majors", average: 150),
			.init(id: UUID(1), name: "Minors", average: 275),
		])
	}

	func testList_WhenSeriesExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(), excludeFromStatistics: .exclude)
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200)
		let db = try initializeDatabase(
			withLeagues: .custom([league1]),
			withSeries: .custom([series1, series2]),
			withGames: .custom([game1, game2])
		)

		// Fetching the league
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the leagues with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Majors", average: 100),
		])
	}

	func testList_WhenGameExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date())
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date())
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200, excludeFromStatistics: .exclude)
		let db = try initializeDatabase(
			withLeagues: .custom([league1]),
			withSeries: .custom([series1, series2]),
			withGames: .custom([game1, game2])
		)

		// Fetching the league
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the league with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Majors", average: 100),
		])
	}

	// MARK: - Series Host

	func testSeriesHost_WhenLeagueExists_ReturnsLeague() async throws {
		// Given a database with one league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league1]))

		// Fetching the league
		let league = try await withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.seriesHost(UUID(0))
		}

		// Returns the league
		XCTAssertEqual(
			league,
			.init(
				id: UUID(0),
				name: "Majors",
				numberOfGames: 4,
				alley: nil,
				excludeFromStatistics: .include
			)
		)
	}

	func testSeriesHost_WhenLeagueNotExists_ReturnsNil() async throws {
		// Given a database with no leagues
		let db = try initializeDatabase(withLeagues: nil)

		// Fetching the league
		let league = try await withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.seriesHost(UUID(0))
		}

		// Returns nothing
		XCTAssertNil(league)
	}

	// MARK: - Create

	func testCreate_WhenLeagueExists_ThrowsError() async throws {
		// Given a database with an existing league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", additionalPinfall: nil, additionalGames: nil)
		let db = try initializeDatabase(withLeagues: .custom([league1]))

		// Creating the league
		let new = League.Create(
			bowlerId: UUID(0),
			id: UUID(0),
			name: "Minors",
			recurrence: league1.recurrence,
			numberOfGames: league1.numberOfGames,
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: league1.excludeFromStatistics
		)
		await assertThrowsError(ofType: DatabaseError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.leagues = .liveValue
			} operation: {
				try await self.leagues.create(new)
			}
		}

		// Does not update the database
		let updated = try await db.read { try League.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Majors")

		// Does not insert any records
		let count = try await db.read { try League.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testCreate_WhenLeagueNotExists_CreatesLeague() async throws {
		// Given a database with no leagues
		let db = try initializeDatabase(withAlleys: .default, withBowlers: .default, withLeagues: nil)

		// Creating a league
		let new = League.Create(
			bowlerId: UUID(0),
			id: UUID(0),
			name: "Minors",
			recurrence: .once,
			numberOfGames: 1,
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: .exclude
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.uuid = .incrementing
			$0.date = .constant(Date(timeIntervalSince1970: 123_456_000))
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.create(new)
		}

		// Inserted the record
		let exists = try await db.read { try League.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let created = try await db.read { try League.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(created?.id, UUID(0))
		XCTAssertEqual(created?.name, "Minors")
		XCTAssertEqual(created?.numberOfGames, 1)
	}

	// MARK: - Update

	func testUpdate_WhenLeagueExists_UpdatesLeague() async throws {
		// Given a database with an existing league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", additionalPinfall: nil, additionalGames: nil)
		let db = try initializeDatabase(withLeagues: .custom([league1]))

		// Editing the league
		let existing = League.Edit(
			id: UUID(0),
			recurrence: .repeating,
			numberOfGames: 4,
			name: "Minors",
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: league1.excludeFromStatistics
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.update(existing)
		}

		// Updates the database
		let updated = try await db.read { try League.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Minors")

		// Does not insert any records
		let count = try await db.read { try League.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenLeagueNotExists_ThrowsError() async throws {
		// Given a database with no leagues
		let db = try initializeDatabase(withLeagues: nil)

		// Editing a league
		let existing = League.Edit(
			id: UUID(0),
			recurrence: .once,
			numberOfGames: 1,
			name: "Minors",
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: .exclude
		)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.leagues = .liveValue
			} operation: {
				try await self.leagues.update(existing)
			}
		}

		// Does not insert the record
		let exists = try await db.read { try League.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(exists)

		// Does not insert any records
		let count = try await db.read { try League.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: - Edit

	func testEdit_WhenLeagueExists_ReturnsLeague() async throws {
		// Given a database with one league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league1]))

		// Editing the league
		let league = try await withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.edit(UUID(0))
		}

		// Returns the league
		XCTAssertEqual(
			league,
			.init(
				id: UUID(0),
				recurrence: .repeating,
				numberOfGames: 4,
				name: "Majors",
				additionalPinfall: nil,
				additionalGames: nil,
				excludeFromStatistics: .include
			)
		)
	}

	func testEdit_WhenLeagueNotExists_ReturnsNil() async throws {
		// Given a database with no leagues
		let db = try initializeDatabase(withLeagues: nil)

		// Editing the league
		let league = try await withDependencies {
			$0.database.reader = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.edit(UUID(0))
		}

		// Returns nil
		XCTAssertNil(league)
	}

	// MARK: - Delete

	func testDelete_WhenIdExists_DeletesLeague() async throws {
		// Given a database with 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		let db = try initializeDatabase(withLeagues: .custom([league1, league2]))

		// Deleting the first league
		try await withDependencies {
			$0.database.writer = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try League.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other league intact
		let otherExists = try await db.read { try League.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league1]))

		// Deleting a non-existent league
		try await withDependencies {
			$0.database.writer = { db }
			$0.leagues = .liveValue
		} operation: {
			try await self.leagues.delete(UUID(1))
		}

		// Leaves the league
		let exists = try await db.read { try League.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}

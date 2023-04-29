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
		let db = try await initializeDatabase(withLeagues: .custom([league1, league2]))

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
		XCTAssertEqual(fetched, [.init(league1), .init(league2)])
	}

	func testList_FilterByRecurrence_ReturnsMatchingLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", recurrence: .once)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", recurrence: .repeating)
		let db = try await initializeDatabase(withLeagues: .custom([league1, league2]))

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
		XCTAssertEqual(fetched, [.init(league1)])
	}

	func testList_FilterByBowler_ReturnsBowlerLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(bowlerId: UUID(0), id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(bowlerId: UUID(1), id: UUID(1), name: "Minors")
		let db = try await initializeDatabase(withLeagues: .custom([league1, league2]))

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
		XCTAssertEqual(fetched, [.init(league1)])
	}

	func testList_SortsByName() async throws {
		// Given a database with three leagues
		let league1 = League.Database.mock(id: UUID(0), name: "B League")
		let league2 = League.Database.mock(id: UUID(1), name: "A League")
		let league3 = League.Database.mock(id: UUID(2), name: "C League")
		let db = try await initializeDatabase(withLeagues: .custom([league1, league2, league3]))

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
		XCTAssertEqual(fetched, [.init(league2), .init(league1), .init(league3)])
	}

	func testList_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: UUID(0), name: "B League")
		let league2 = League.Database.mock(id: UUID(1), name: "A League")
		let db = try await initializeDatabase(withLeagues: .custom([league1, league2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
			$0.leagues = .liveValue
		} operation: {
			self.leagues.list(bowledBy: UUID(0), ordering: .byRecentlyUsed)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues sorted by recently used ids
		XCTAssertEqual(fetched, [.init(league1), .init(league2)])
	}

	// MARK: - Series Host

	func testSeriesHost_WhenLeagueExists_ReturnsLeague() async throws {
		// Given a database with one league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

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

	func testSeriesHost_WhenLeagueExistsWithAlley_ReturnsLeagueWithAlley() async throws {
		// Given a database with one league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", alleyId: UUID(0))
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

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
				alley: .init(
					id: UUID(0),
					name: "Skyview",
					material: .wood,
					pinFall: .strings,
					mechanism: .dedicated,
					pinBase: nil,
					location: nil
				),
				excludeFromStatistics: .include
			)
		)
	}

	func testSeriesHost_WhenLeagueNotExists_ReturnsNil() async throws {
		// Given a database with no leagues
		let db = try await initializeDatabase(withLeagues: nil)

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
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

		// Creating the league
		let new = League.Create(
			bowlerId: UUID(0),
			id: UUID(0),
			name: "Minors",
			recurrence: league1.recurrence,
			numberOfGames: league1.numberOfGames,
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: league1.excludeFromStatistics,
			location: nil
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
		let db = try await initializeDatabase(withAlleys: .default, withBowlers: .default, withLeagues: nil)

		// Creating a league
		let new = League.Create(
			bowlerId: UUID(0),
			id: UUID(0),
			name: "Minors",
			recurrence: .once,
			numberOfGames: 1,
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: .exclude,
			location: .init(
				id: UUID(0),
				name: "Skyview",
				material: .wood,
				pinFall: .strings,
				mechanism: .dedicated,
				pinBase: nil,
				location: nil
			)
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
		XCTAssertEqual(created?.alleyId, UUID(0))
	}

	// MARK: - Update

	func testUpdate_WhenLeagueExists_UpdatesLeague() async throws {
		// Given a database with an existing league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", additionalPinfall: nil, additionalGames: nil)
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

		// Editing the league
		let existing = League.Edit(
			id: UUID(0),
			recurrence: .repeating,
			numberOfGames: 4,
			name: "Minors",
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: league1.excludeFromStatistics,
			location: nil
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
		let db = try await initializeDatabase(withLeagues: nil)

		// Editing a league
		let existing = League.Edit(
			id: UUID(0),
			recurrence: .once,
			numberOfGames: 1,
			name: "Minors",
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: .exclude,
			location: nil
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
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

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
				excludeFromStatistics: .include,
				location: nil
			)
		)
	}

	func testEdit_WhenLeagueNotExists_ReturnsNil() async throws {
		// Given a database with no leagues
		let db = try await initializeDatabase(withLeagues: nil)

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

	func testEdit_WhenLeagueHasAlley_ReturnLeagueWithAlley() async throws {
		// Given a database with one league
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", alleyId: UUID(0))
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

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
				excludeFromStatistics: .include,
				location: .init(
					id: UUID(0),
					name: "Skyview",
					material: .wood,
					pinFall: .strings,
					mechanism: .dedicated,
					pinBase: nil,
					location: nil
				)
			)
		)
	}

	// MARK: - Delete

	func testDelete_WhenIdExists_DeletesLeague() async throws {
		// Given a database with 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		let db = try await initializeDatabase(withLeagues: .custom([league1, league2]))

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
		let db = try await initializeDatabase(withLeagues: .custom([league1]))

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

extension League.Database {
	static func mock(
		bowlerId: Bowler.ID = UUID(0),
		id: ID,
		name: String,
		recurrence: League.Recurrence = .repeating,
		numberOfGames: Int? = League.DEFAULT_NUMBER_OF_GAMES,
		additionalPinfall: Int? = nil,
		additionalGames: Int? = nil,
		excludeFromStatistics: League.ExcludeFromStatistics = .include,
		alleyId: Alley.ID? = nil
	) -> Self {
		.init(
			bowlerId: bowlerId,
			id: id,
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			excludeFromStatistics: excludeFromStatistics,
			alleyId: alleyId
		)
	}
}

extension League.Summary {
	init(_ from: League.Database) {
		self.init(
			id: from.id,
			name: from.name
		)
	}
}

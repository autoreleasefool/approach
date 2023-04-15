import DatabaseModelsLibrary
@testable import DatabaseService
import Dependencies
import GRDB
@testable import LeaguesRepository
@testable import LeaguesRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import XCTest

@MainActor
final class LeaguesRepositoryTests: XCTestCase {

	let bowlerId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!
	let bowlerId2 = UUID(uuidString: "00000000-0000-0000-0000-00000000000B")!

	let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
	let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
	let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

	func testList_ReturnsAllLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: id1, name: "Majors")
		let league2 = League.Database.mock(id: id2, name: "Minors")
		let db = try await initializeDatabase(inserting: [league1, league2])

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
		} operation: {
			LeaguesRepository.liveValue.list(bowledBy: bowlerId1, ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues
		XCTAssertEqual(fetched, [.init(league1), .init(league2)])
	}

	func testList_FilterByRecurrence_ReturnsMatchingLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: id1, name: "Majors", recurrence: .once)
		let league2 = League.Database.mock(id: id2, name: "Minors", recurrence: .repeating)
		let db = try await initializeDatabase(inserting: [league1, league2])

		// Fetching the leagues by recurrence
		let leagues = withDependencies {
			$0.database.reader = { db }
		} operation: {
			LeaguesRepository.liveValue.list(bowledBy: bowlerId1, withRecurrence: .once, ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one league
		XCTAssertEqual(fetched, [.init(league1)])
	}

	func testList_FilterByBowler_ReturnsBowlerLeagues() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(bowler: bowlerId1, id: id1, name: "Majors")
		let league2 = League.Database.mock(bowler: bowlerId2, id: id2, name: "Minors")
		let db = try await initializeDatabase(inserting: [league1, league2])

		// Fetching the leagues by bowler
		let leagues = withDependencies {
			$0.database.reader = { db }
		} operation: {
			LeaguesRepository.liveValue.list(bowledBy: bowlerId1, ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one league
		XCTAssertEqual(fetched, [.init(league1)])
	}

	func testList_SortsByName() async throws {
		// Given a database with three leagues
		let league1 = League.Database.mock(id: id1, name: "B League")
		let league2 = League.Database.mock(id: id2, name: "A League")
		let league3 = League.Database.mock(id: id3, name: "C League")
		let db = try await initializeDatabase(inserting: [league1, league2, league3])

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
		} operation: {
			LeaguesRepository.liveValue.list(bowledBy: bowlerId1, ordering: .byName)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues sorted by name
		XCTAssertEqual(fetched, [.init(league2), .init(league1), .init(league3)])
	}

	func testList_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with two leagues
		let league1 = League.Database.mock(id: id1, name: "B League")
		let league2 = League.Database.mock(id: id2, name: "A League")
		let db = try await initializeDatabase(inserting: [league1, league2])

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([id1, id2])

		// Fetching the leagues
		let leagues = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
		} operation: {
			LeaguesRepository.liveValue.list(bowledBy: bowlerId1, ordering: .byRecentlyUsed)
		}
		var iterator = leagues.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the leagues sorted by recently used ids
		XCTAssertEqual(fetched, [.init(league1), .init(league2)])
	}

	func testSave_WhenLeagueExists_UpdatesLeague() async throws {
		// Given a database with an existing league
		let league1 = League.Database.mock(id: id1, name: "Majors", additionalPinfall: nil, additionalGames: nil)
		let db = try await initializeDatabase(inserting: [league1])

		// Editing the league
		let editable = League.Editable(
			bowler: bowlerId1,
			id: id1,
			name: "Minors",
			recurrence: league1.recurrence,
			numberOfGames: league1.numberOfGames,
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: league1.excludeFromStatistics,
			alley: league1.alley
		)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LeaguesRepository.liveValue.save(editable)
		}

		// Updates the database
		let updated = try await db.read { try League.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Minors")
		XCTAssertEqual(updated?.additionalGames, 123)
		XCTAssertEqual(updated?.additionalPinfall, 123)

		// Does not insert any records
		let count = try await db.read { try League.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testSave_WhenLeagueNotExists_SavesNewLeague() async throws {
		// Given a database with no leagues
		let db = try await initializeDatabase(inserting: [])

		// Saving a league
		let editable = League.Editable(
			bowler: bowlerId1,
			id: id1,
			name: "Minors",
			recurrence: .once,
			numberOfGames: 1,
			additionalPinfall: 123,
			additionalGames: 123,
			excludeFromStatistics: .exclude,
			alley: nil
		)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LeaguesRepository.liveValue.save(editable)
		}

		// Inserted the record
		let exists = try await db.read { try League.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try League.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Minors")
		XCTAssertEqual(updated?.numberOfGames, 1)
	}

	func testEdit_WhenLeagueExists_ReturnsLeague() async throws {
		// Given a database with one league
		let league1 = League.Database.mock(id: id1, name: "Majors")
		let db = try await initializeDatabase(inserting: [league1])

		// Editing the league
		let league = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await LeaguesRepository.liveValue.edit(id1)
		}

		// Returns the league
		XCTAssertEqual(
			league,
			.init(
				bowler: bowlerId1,
				id: id1,
				name: "Majors",
				recurrence: .repeating,
				numberOfGames: 4,
				additionalPinfall: nil,
				additionalGames: nil,
				excludeFromStatistics: .include,
				alley: nil
			)
		)
	}

	func testEdit_WhenLeagueNotExists_ReturnsNil() async throws {
		// Given a database with no leagues
		let db = try await initializeDatabase(inserting: [])

		// Editing the league
		let league = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await LeaguesRepository.liveValue.edit(id1)
		}

		// Returns nil
		XCTAssertNil(league)
	}

	func testDelete_WhenIdExists_DeletesLeague() async throws {
		// Given a database with 2 leagues
		let league1 = League.Database.mock(id: id1, name: "Majors")
		let league2 = League.Database.mock(id: id2, name: "Minors")
		let db = try await initializeDatabase(inserting: [league1, league2])

		// Deleting the first league
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LeaguesRepository.liveValue.delete(self.id1)
		}

		// Updates the database
		let deletedExists = try await db.read { try League.Database.exists($0, id: self.id1) }
		XCTAssertFalse(deletedExists)

		// And leaves the other league intact
		let otherExists = try await db.read { try League.Database.exists($0, id: self.id2) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 league
		let league1 = League.Database.mock(id: id1, name: "Majors")
		let db = try await initializeDatabase(inserting: [league1])

		// Deleting a non-existent league
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LeaguesRepository.liveValue.delete(self.id2)
		}

		// Leaves the league
		let exists = try await db.read { try League.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)
	}

	private func initializeDatabase(
		inserting leagues: [League.Database] = []
	) async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)

		let bowlers = [
			Bowler.Database(id: bowlerId1, name: "Joseph", status: .playable),
			Bowler.Database(id: bowlerId2, name: "Sarah", status: .playable)
		]

		try await dbQueue.write {
			for bowler in bowlers {
				try bowler.insert($0)
			}
			for league in leagues {
				try league.insert($0)
			}
		}

		return dbQueue
	}
}

extension League.Database {
	static func mock(
		bowler: Bowler.ID = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!,
		id: ID,
		name: String,
		recurrence: League.Recurrence = .repeating,
		numberOfGames: Int? = League.DEFAULT_NUMBER_OF_GAMES,
		additionalPinfall: Int? = nil,
		additionalGames: Int? = nil,
		excludeFromStatistics: League.ExcludeFromStatistics = .include,
		alley: Alley.ID? = nil
	) -> Self {
		.init(
			bowler: bowler,
			id: id,
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			excludeFromStatistics: excludeFromStatistics,
			alley: alley
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

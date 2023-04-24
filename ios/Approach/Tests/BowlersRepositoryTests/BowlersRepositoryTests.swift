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

	let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
	let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
	let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

	// MARK: Playable

	func testPlayable_ReturnsOnlyPlayable() async throws {
		// Given a database with a bowler and opponent
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: id2, name: "Sarah", status: .opponent)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.playable(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns only the playable bowler
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph")])
	}

	func testPlayable_SortsByName() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: id2, name: "Audriana", status: .playable)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.playable(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by name
		XCTAssertEqual(fetched, [.init(id: id2, name: "Audriana"), .init(id: id1, name: "Joseph")])
	}

	func testPlayable_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: id2, name: "Audriana", status: .playable)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([id1, id2])

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.playable(ordered: .byRecentlyUsed)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by recently used ids
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph"), .init(id: id2, name: "Audriana")])
	}

	// MARK: Opponents

	func testOpponents_ReturnsPlayablesAndOpponents() async throws {
		// Given a database with a bowler and an opponent
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .opponent)
		let bowler2 = Bowler.Database(id: id2, name: "Sarah", status: .playable)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

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
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph"), .init(id: id2, name: "Sarah")])
	}

	func testOpponents_SortsByName() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .opponent)
		let bowler2 = Bowler.Database(id: id2, name: "Audriana", status: .opponent)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

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
		XCTAssertEqual(fetched, [.init(id: id2, name: "Audriana"), .init(id: id1, name: "Joseph")])
	}

	func testOpponents_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .opponent)
		let bowler2 = Bowler.Database(id: id2, name: "Audriana", status: .opponent)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([id1, id2])

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
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph"), .init(id: id2, name: "Audriana")])
	}

	// MARK: Create

	func testCreate_WhenBowlerExists_ThrowsError() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .opponent)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1]))

		// Create the bowler
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = Bowler.Create(id: id1, name: "Joe", status: .playable)
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
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Joseph")
		XCTAssertEqual(updated?.status, .opponent)
	}

	func testCreate_WhenBowlerNotExists_CreatesBowler() async throws {
		// Given a database with no bowlers
		let db = try await initializeDatabase()

		// Creating a bowler
		let create = Bowler.Create(id: id1, name: "Joe", status: .playable)
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Bowler.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.status, .playable)
	}

	// MARK: Update

	func testUpdate_WhenBowlerExists_UpdatesBowler() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .opponent)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1]))

		// Editing the bowler
		let editable = Bowler.Edit(id: id1, name: "Joe")
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.status, .opponent)

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenBowlerNotExists_ThrowError() async throws {
		// Given a database with no bowlers
		let db = try await initializeDatabase()

		// Updating a bowler
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Bowler.Edit(id: id1, name: "Joe")
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
		let bowler = Bowler.Database(id: id1, name: "Joseph", status: .playable)
		let db = try await initializeDatabase(withBowlers: .custom([bowler]))

		// Editing the bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.edit(id1)
		}

		// Returns the bowler
		XCTAssertEqual(editable, .init(id: id1, name: "Joseph"))
	}

	func testEdit_WhenBowlerNotExists_ReturnsNil() async throws {
		// Given a database with no bowlers
		let db = try await initializeDatabase()

		// Editing a bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.edit(id1)
		}

		// Returns nil
		XCTAssertNil(editable)
	}

	// MARK: Delete

	func testDelete_WhenIdExists_DeletesBowler() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.Database(id: id2, name: "Sarah", status: .opponent)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Deleting the first bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.delete(self.id1)
		}

		// Updates the database
		let deletedExists = try await db.read { try Bowler.Database.exists($0, id: self.id1) }
		XCTAssertFalse(deletedExists)

		// And leaves the other bowler intact
		let otherExists = try await db.read { try Bowler.Database.exists($0, id: self.id2) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1
		let bowler1 = Bowler.Database(id: id1, name: "Joseph", status: .playable)
		let db = try await initializeDatabase(withBowlers: .custom([bowler1]))

		// Deleting a non-existent bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.delete(self.id2)
		}

		// Leaves the bowler
		let exists = try await db.read { try Bowler.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)
	}
}

@testable import BowlersRepository
@testable import BowlersRepositoryInterface
import DatabaseModelsLibrary
@testable import DatabaseService
import Dependencies
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import TestUtilitiesLibrary
import XCTest

@MainActor
final class BowlersRepositoryTests: XCTestCase {

	let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
	let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
	let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

	func testPlayable_ReturnsOnlyPlayable() async throws {
		// Given a database with a bowler and opponent
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Sarah", status: .opponent)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
		} operation: {
			BowlersRepository.liveValue.playable(.init(ordering: .byName))
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns only the playable bowler
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph")])
	}

	func testPlayable_SortsByName() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Audriana", status: .playable)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
		} operation: {
			BowlersRepository.liveValue.playable(.init(ordering: .byName))
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by name
		XCTAssertEqual(fetched, [.init(id: id2, name: "Audriana"), .init(id: id1, name: "Joseph")])
	}

	func testPlayable_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Audriana", status: .playable)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([id1, id2])

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
		} operation: {
			BowlersRepository.liveValue.playable(.init(ordering: .byRecentlyUsed))
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by recently used ids
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph"), .init(id: id2, name: "Audriana")])
	}

	func testOpponents_ReturnsPlayablesAndOpponents() async throws {
		// Given a database with a bowler and an opponent
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .opponent)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Sarah", status: .playable)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
		} operation: {
			BowlersRepository.liveValue.opponents(.init(ordering: .byName))
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns both bowlers
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph"), .init(id: id2, name: "Sarah")])
	}

	func testOpponents_SortsByName() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .opponent)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Audriana", status: .opponent)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
		} operation: {
			BowlersRepository.liveValue.opponents(.init(ordering: .byName))
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the opponents sorted by name
		XCTAssertEqual(fetched, [.init(id: id2, name: "Audriana"), .init(id: id1, name: "Joseph")])
	}

	func testOpponents_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .opponent)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Audriana", status: .opponent)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.streamWithContinuation()
		recentContinuation.yield([id1, id2])

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsedService.observeRecentlyUsedIds = { _ in recentStream }
		} operation: {
			// with `byRecentlyUsed` ordering
			BowlersRepository.liveValue.opponents(.init(ordering: .byRecentlyUsed))
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the opponents sorted by recently used
		XCTAssertEqual(fetched, [.init(id: id1, name: "Joseph"), .init(id: id2, name: "Audriana")])
	}

	func testCreate_WhenBowlerExists_ThrowsError() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .opponent)
		let db = try await initializeDatabase(inserting: [bowler1])

		// Create the bowler
		await assertThrowsError {
			let create = Bowler.Create(id: id1, name: "Joe", status: .playable)
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await BowlersRepository.liveValue.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Bowler.DatabaseModel.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Bowler.DatabaseModel.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Joseph")
		XCTAssertEqual(updated?.status, .opponent)
	}

	func testCreate_WhenBowlerNotExists_CreatesBowler() async throws {
		// Given a database with no bowlers
		let db = try await initializeDatabase(inserting: [])

		// Creating a bowler
		let create = Bowler.Create(id: id1, name: "Joe", status: .playable)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await BowlersRepository.liveValue.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Bowler.DatabaseModel.exists($0, id: self.id1) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Bowler.DatabaseModel.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.status, .playable)
	}

	func testUpdate_WhenBowlerExists_UpdatesBowler() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .opponent)
		let db = try await initializeDatabase(inserting: [bowler1])

		// Editing the bowler
		let editable = Bowler.Edit(id: id1, name: "Joe")
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await BowlersRepository.liveValue.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Bowler.DatabaseModel.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.status, .opponent)

		// Does not insert any records
		let count = try await db.read { try Bowler.DatabaseModel.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenBowlerNotExists_ThrowError() async throws {
		// Given a database with no bowlers
		let db = try await initializeDatabase(inserting: [])

		// Updating a bowler
		await assertThrowsError {
			let editable = Bowler.Edit(id: id1, name: "Joe")
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await BowlersRepository.liveValue.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Bowler.DatabaseModel.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testEdit_WhenBowlerExists_ReturnsBowler() async throws {
		// Given a database with a bowler
		let bowler = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .playable)
		let db = try await initializeDatabase(inserting: [bowler])

		// Editing the bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await BowlersRepository.liveValue.edit(id1)
		}

		// Returns the bowler
		XCTAssertEqual(editable, .init(id: id1, name: "Joseph"))
	}

	func testEdit_WhenBowlerNotExists_ReturnsNil() async throws {
		// Given a database with no bowlers
		let db = try await initializeDatabase(inserting: [])

		// Editing a bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await BowlersRepository.liveValue.edit(id1)
		}

		// Returns nil
		XCTAssertNil(editable)
	}

	func testDelete_WhenIdExists_DeletesBowler() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .playable)
		let bowler2 = Bowler.DatabaseModel(id: id2, name: "Sarah", status: .opponent)
		let db = try await initializeDatabase(inserting: [bowler1, bowler2])

		// Deleting the first bowler
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await BowlersRepository.liveValue.delete(self.id1)
		}

		// Updates the database
		let deletedExists = try await db.read { try Bowler.DatabaseModel.exists($0, id: self.id1) }
		XCTAssertFalse(deletedExists)

		// And leaves the other bowler intact
		let otherExists = try await db.read { try Bowler.DatabaseModel.exists($0, id: self.id2) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1
		let bowler1 = Bowler.DatabaseModel(id: id1, name: "Joseph", status: .playable)
		let db = try await initializeDatabase(inserting: [bowler1])

		// Deleting a non-existent bowler
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await BowlersRepository.liveValue.delete(self.id2)
		}

		// Leaves the bowler
		let exists = try await db.read { try Bowler.DatabaseModel.exists($0, id: self.id1) }
		XCTAssertTrue(exists)
	}

	private func initializeDatabase(
		inserting bowlers: [Bowler.DatabaseModel] = []
	) async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)

		try await dbQueue.write {
			for bowler in bowlers {
				try bowler.insert($0)
			}
		}

		return dbQueue
	}
}

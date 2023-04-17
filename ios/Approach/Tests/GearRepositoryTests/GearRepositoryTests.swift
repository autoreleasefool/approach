import DatabaseModelsLibrary
@testable import DatabaseService
import Dependencies
@testable import GearRepository
@testable import GearRepositoryInterface
import GRDB
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestUtilitiesLibrary
import XCTest

@MainActor
final class GearRepositoryTests: XCTestCase {

	let bowlerId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!
	let bowlerId2 = UUID(uuidString: "00000000-0000-0000-0000-00000000000B")!

	let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
	let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

	func testList_ReturnsAllGear() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: id1, name: "Yellow")
		let gear2 = Gear.Database.mock(id: id2, name: "Blue")
		let db = try await initializeDatabase(inserting: [gear1, gear2])

		// Fetching the gear
		let gear = withDependencies {
			$0.database.reader = { db }
		} operation: {
			GearRepository.liveValue.list(ordered: .byName)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the gear
		XCTAssertEqual(fetched, [
			.init(id: id2, name: "Blue", kind: .bowlingBall, ownerName: "Joseph"),
			.init(id: id1, name: "Yellow", kind: .bowlingBall, ownerName: "Joseph"),
		])
	}

	func testList_FilterByKind_ReturnsMatchingGear() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: id1, name: "Yellow", kind: .bowlingBall)
		let gear2 = Gear.Database.mock(id: id2, name: "Blue", kind: .towel)
		let db = try await initializeDatabase(inserting: [gear1, gear2])

		// Fetching the gear
		let gear = withDependencies {
			$0.database.reader = { db }
		} operation: {
			GearRepository.liveValue.list(ofKind: .bowlingBall, ordered: .byName)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the matching gear
		XCTAssertEqual(fetched, [
			.init(id: id1, name: "Yellow", kind: .bowlingBall, ownerName: "Joseph"),
		])
	}

	func testList_FilterByBowler_ReturnsMatchingGear() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: id1, name: "Yellow", kind: .bowlingBall, bowlerId: bowlerId1)
		let gear2 = Gear.Database.mock(id: id2, name: "Blue", kind: .towel, bowlerId: bowlerId2)
		let db = try await initializeDatabase(inserting: [gear1, gear2])

		// Fetching the gear
		let gear = withDependencies {
			$0.database.reader = { db }
		} operation: {
			GearRepository.liveValue.list(ownedBy: bowlerId1, ordered: .byName)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the matching gear
		XCTAssertEqual(fetched, [
			.init(id: id1, name: "Yellow", kind: .bowlingBall, ownerName: "Joseph"),
		])
	}

	func testCreate_WhenGearExists_ThrowsError() async throws {
		// Given a database with an existing gear
		let gear1 = Gear.Database.mock(id: id1, name: "Yellow")
		let db = try await initializeDatabase(inserting: [gear1])

		// Create the gear
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = Gear.Create(id: id1, name: "Blue", kind: .towel, owner: .init(id: bowlerId2, name: "Sarah"))
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await GearRepository.liveValue.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Gear.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Gear.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Yellow")
		XCTAssertEqual(updated?.kind, .bowlingBall)
		XCTAssertEqual(updated?.bowlerId, bowlerId1)
	}

	func testCreate_WhenGearNotExists_CreatesGear() async throws {
		// Given a database with no gear
		let db = try await initializeDatabase(inserting: [])

		// Creating a gear
		let create = Gear.Create(id: id1, name: "Yellow", kind: .bowlingBall, owner: nil)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GearRepository.liveValue.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Gear.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Gear.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Yellow")
		XCTAssertEqual(updated?.kind, .bowlingBall)
	}

	func testUpdate_WhenGearExists_UpdatesGear() async throws {
		// Given a database with an existing gear
		let gear1 = Gear.Database(id: id1, name: "Yellow", kind: .bowlingBall, bowlerId: nil)
		let db = try await initializeDatabase(inserting: [gear1])

		// Editing the gear
		let editable = Gear.Edit(id: id1, kind: .bowlingBall, name: "Blue", owner: .init(id: bowlerId1, name: "Sarah"))
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GearRepository.liveValue.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Gear.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.name, "Blue")
		XCTAssertEqual(updated?.kind, .bowlingBall)
		XCTAssertEqual(updated?.bowlerId, bowlerId1)

		// Does not insert any records
		let count = try await db.read { try Gear.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenGearNotExists_ThrowError() async throws {
		// Given a database with no gear
		let db = try await initializeDatabase(inserting: [])

		// Updating a gear
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Gear.Edit(id: id1, kind: .bowlingBall, name: "Blue", owner: nil)
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await GearRepository.liveValue.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Gear.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testEdit_WhenGearExists_ReturnsGear() async throws {
		// Given a database with a gear
		let gear = Gear.Database(id: id1, name: "Yellow", kind: .bowlingBall, bowlerId: bowlerId1)
		let db = try await initializeDatabase(inserting: [gear])

		// Editing the gear
		let editable = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await GearRepository.liveValue.edit(id1)
		}

		// Returns the gear
		XCTAssertEqual(
			editable,
			.init(id: id1, kind: .bowlingBall, name: "Yellow", owner: .init(id: bowlerId1, name: "Joseph"))
		)
	}

	func testEdit_WhenGearNotExists_ReturnsNil() async throws {
		// Given a database with no gear
		let db = try await initializeDatabase(inserting: [])

		// Editing a gear
		let editable = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await GearRepository.liveValue.edit(id1)
		}

		// Returns nil
		XCTAssertNil(editable)
	}

	func testDelete_WhenIdExists_DeletesGear() async throws {
		// Given a database with 2 gear
		let gear1 = Gear.Database.mock(id: id1, name: "Yellow")
		let gear2 = Gear.Database.mock(id: id2, name: "Blue")
		let db = try await initializeDatabase(inserting: [gear1, gear2])

		// Deleting the first gear
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GearRepository.liveValue.delete(self.id1)
		}

		// Updates the database
		let deletedExists = try await db.read { try Gear.Database.exists($0, id: self.id1) }
		XCTAssertFalse(deletedExists)

		// And leaves the other gear intact
		let otherExists = try await db.read { try Gear.Database.exists($0, id: self.id2) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1
		let gear1 = Gear.Database.mock(id: id1, name: "Yellow")
		let db = try await initializeDatabase(inserting: [gear1])

		// Deleting a non-existent gear
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await GearRepository.liveValue.delete(self.id2)
		}

		// Leaves the gear
		let exists = try await db.read { try Gear.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)
	}

	private func initializeDatabase(
		inserting gear: [Gear.Database] = []
	) async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)

		let bowlers = [
			Bowler.Database(id: bowlerId1, name: "Joseph", status: .playable),
			Bowler.Database(id: bowlerId2, name: "Sarah", status: .playable),
		]

		try await dbQueue.write {
			for bowler in bowlers {
				try bowler.insert($0)
			}
			for gear in gear {
				try gear.insert($0)
			}
		}

		return dbQueue
	}
}

extension Gear.Database {
	static func mock(
		id: ID,
		name: String,
		kind: Gear.Kind = .bowlingBall,
		bowlerId: Bowler.ID? = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!
	) -> Self {
		.init(
			id: id,
			name: name,
			kind: kind,
			bowlerId: bowlerId
		)
	}
}

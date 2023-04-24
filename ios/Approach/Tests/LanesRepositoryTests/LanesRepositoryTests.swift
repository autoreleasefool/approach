import DatabaseModelsLibrary
import Dependencies
import GRDB
@testable import LanesRepository
@testable import LanesRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class LanesRepositoryTests: XCTestCase {
	@Dependency(\.lanes) var lanes

	// MARK: List

	func testList_ReturnsLanes() async throws {
		// Given a database with three lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .noWall)
		let lane3 = Lane.Database(alleyId: UUID(1), id: UUID(2), label: "3", position: .noWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2, lane3]))

		// Getting the lanes
		let lanes = withDependencies {
			$0.database.reader = { db }
			$0.lanes = .liveValue
		} operation: {
			self.lanes.list(nil)
		}

		var iterator = lanes.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alley lanes
		XCTAssertEqual(
			fetched,
			[
				.init(id: UUID(0), label: "1", position: .leftWall),
				.init(id: UUID(1), label: "2", position: .noWall),
				.init(id: UUID(2), label: "3", position: .noWall),
			]
		)
	}

	func testList_ReturnsLanesMatchingAlley() async throws {
		// Given a database with three lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .noWall)
		let lane3 = Lane.Database(alleyId: UUID(1), id: UUID(2), label: "3", position: .noWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2, lane3]))

		// Getting the lanes
		let lanes = withDependencies {
			$0.database.reader = { db }
			$0.lanes = .liveValue
		} operation: {
			self.lanes.list(UUID(0))
		}

		var iterator = lanes.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alley lanes
		XCTAssertEqual(
			fetched,
			[.init(id: UUID(0), label: "1", position: .leftWall), .init(id: UUID(1), label: "2", position: .noWall)]
		)
	}

	// MARK: Edit

	func testEdit_WhenLanesExist_ReturnsLanes() async throws {
		// Given a database with two lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .noWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2]))

		// Editing the lanes
		let lanes = try await withDependencies {
			$0.database.reader = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.edit(UUID(0))
		}

		// Returns the lanes
		XCTAssertEqual(lanes, [
			.init(id: UUID(0), label: "1", position: .leftWall),
			.init(id: UUID(1), label: "2", position: .noWall),
		])
	}

	func testEdit_WhenLanesNotExist_ReturnsEmptyArray() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase(withAlleys: .default, withLanes: nil)

		// Editing the lanes
		let lanes = try await withDependencies {
			$0.database.reader = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.edit(UUID(0))
		}

		// Returns an empty array
		XCTAssertEqual(lanes, [])
	}

	func testEdit_ReturnsLanesForAlley() async throws {
		// Given a database with two lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(1), id: UUID(1), label: "2", position: .noWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2]))

		// Editing the lanes
		let lanes = try await withDependencies {
			$0.database.reader = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.edit(UUID(0))
		}

		// Returns the lane for the alley
		XCTAssertEqual(lanes, [.init(id: UUID(0), label: "1", position: .leftWall)])
	}

	// MARK: Create

	func testCreate_WhenLaneExists_ThrowsError() async throws {
		// Given a database with one lane
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1]))

		// Creating the lane
		let created = Lane.Create(alleyId: UUID(0), id: UUID(0), label: "2", position: .noWall)
		await assertThrowsError(ofType: DatabaseError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.lanes = .liveValue
			} operation: {
				try await self.lanes.create([created])
			}
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Lane.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.label, "1")
		XCTAssertEqual(updated?.position, .leftWall)
	}

	func testCreate_WhenLaneNotExists_CreatesLane() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase(withAlleys: .default, withLanes: nil)

		let created = Lane.Create(alleyId: UUID(0), id: UUID(0), label: "1", position: .noWall)
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.create([created])
		}

		// Inserts one record
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Creates the lane
		let record = try await db.read { try Lane.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(record?.id, UUID(0))
		XCTAssertEqual(record?.label, "1")
		XCTAssertEqual(record?.position, .noWall)
	}

	func testCreate_WhenMultipleLanes_CreatesAllLanes() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase(withAlleys: .default, withLanes: nil)

		let created1 = Lane.Create(alleyId: UUID(0), id: UUID(0), label: "1", position: .noWall)
		let created2 = Lane.Create(alleyId: UUID(0), id: UUID(1), label: "2", position: .leftWall)
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.create([created1, created2])
		}

		// Inserts two records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 2)

		// Creates the lanes
		let record1 = try await db.read { try Lane.Database.fetchOne($0, id: UUID(0)) }
		let record2 = try await db.read { try Lane.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertEqual(record1?.id, UUID(0))
		XCTAssertEqual(record2?.id, UUID(1))
	}

	// MARK: Update

	func testUpdate_WhenLaneExists_UpdatesLane() async throws {
		// Given a database with one lane
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1]))

		let edit = Lane.Edit(id: UUID(0), label: "2", position: .noWall)
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.update([edit])
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the lane
		let record = try await db.read { try Lane.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(record?.id, UUID(0))
		XCTAssertEqual(record?.label, "2")
		XCTAssertEqual(record?.position, .noWall)
	}

	func testUpdate_WhenLaneNotExists_ThrowsError() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase(withAlleys: .default, withLanes: nil)

		// Updating the lane
		let edit = Lane.Edit(id: UUID(0), label: "2", position: .noWall)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.lanes = .liveValue
			} operation: {
				try await self.lanes.update([edit])
			}
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testUpdate_WhenMultipleLanes_UpdatesAllLanes() async throws {
		// Given a database with two lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .leftWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2]))

		let edit1 = Lane.Edit(id: UUID(0), label: "3", position: .noWall)
		let edit2 = Lane.Edit(id: UUID(1), label: "4", position: .noWall)
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.update([edit1, edit2])
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 2)

		// Updates the lanes
		let record1 = try await db.read { try Lane.Database.fetchOne($0, id: UUID(0)) }
		let record2 = try await db.read { try Lane.Database.fetchOne($0, id: UUID(1)) }

		XCTAssertEqual(record1?.id, UUID(0))
		XCTAssertEqual(record1?.label, "3")

		XCTAssertEqual(record2?.id, UUID(1))
		XCTAssertEqual(record2?.label, "4")
	}

	// MARK: Delete

	func testDelete_WhenLaneExists_DeletesLane() async throws {
		// Given a database with 2 lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .leftWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2]))

		// Deleting the first lane
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.delete([UUID(0)])
		}

		// Updates the database
		let deletedExists = try await db.read { try Lane.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other lane intact
		let otherExists = try await db.read { try Lane.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenLaneNotExists_DoesNothing() async throws {
		// Given a database with 1 lane
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1]))

		// Deleting a non-existent lane
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.delete([UUID(1)])
		}

		// Leaves the lane
		let exists = try await db.read { try Lane.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}

	func testDelete_WhenMultipleLanes_DeletesAllLanes() async throws {
		// Given a database with 3 lanes
		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .leftWall)
		let lane3 = Lane.Database(alleyId: UUID(0), id: UUID(2), label: "3", position: .leftWall)
		let db = try await initializeDatabase(withAlleys: .default, withLanes: .custom([lane1, lane2, lane3]))

		// Deleting two lanes
		try await withDependencies {
			$0.database.writer = { db }
			$0.lanes = .liveValue
		} operation: {
			try await self.lanes.delete([UUID(0), UUID(1)])
		}

		// Updates the database
		let deleted1Exists = try await db.read { try Lane.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deleted1Exists)
		let deleted2Exists = try await db.read { try Lane.Database.exists($0, id: UUID(1)) }
		XCTAssertFalse(deleted2Exists)

		// And leaves the other lane intact
		let otherExists = try await db.read { try Lane.Database.exists($0, id: UUID(2)) }
		XCTAssertTrue(otherExists)
	}
}

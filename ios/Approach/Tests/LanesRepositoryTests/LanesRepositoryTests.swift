import DatabaseModelsLibrary
@testable import DatabaseService
import Dependencies
import GRDB
@testable import LanesRepository
@testable import LanesRepositoryInterface
@testable import ModelsLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class LanesRepositoryTests: XCTestCase {

	let alleyId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!
	let alleyId2 = UUID(uuidString: "00000000-0000-0000-0000-00000000000B")!

	let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
	let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
	let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

	// MARK: List

	func testList_ReturnsLanes() async throws {
		// Given a database with three lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId1, id: id2, label: "2", position: .noWall)
		let lane3 = Lane.Database(alleyId: alleyId2, id: id3, label: "3", position: .noWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2, lane3])

		// Getting the lanes
		let lanes = withDependencies {
			$0.database.reader = { db }
		} operation: {
			LanesRepository.liveValue.list(nil)
		}

		var iterator = lanes.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alley lanes
		XCTAssertEqual(
			fetched,
			[
				.init(id: id1, label: "1", position: .leftWall),
				.init(id: id2, label: "2", position: .noWall),
				.init(id: id3, label: "3", position: .noWall),
			]
		)
	}

	func testList_ReturnsLanesMatchingAlley() async throws {
		// Given a database with three lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId1, id: id2, label: "2", position: .noWall)
		let lane3 = Lane.Database(alleyId: alleyId2, id: id3, label: "3", position: .noWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2, lane3])

		// Getting the lanes
		let lanes = withDependencies {
			$0.database.reader = { db }
		} operation: {
			LanesRepository.liveValue.list(self.alleyId1)
		}

		var iterator = lanes.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alley lanes
		XCTAssertEqual(
			fetched,
			[.init(id: id1, label: "1", position: .leftWall), .init(id: id2, label: "2", position: .noWall)]
		)
	}

	// MARK: Edit

	func testEdit_WhenLanesExist_ReturnsLanes() async throws {
		// Given a database with two lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId1, id: id2, label: "2", position: .noWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2])

		// Editing the lanes
		let lanes = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await LanesRepository.liveValue.edit(self.alleyId1)
		}

		// Returns the lanes
		XCTAssertEqual(lanes, [
			.init(id: id1, label: "1", position: .leftWall),
			.init(id: id2, label: "2", position: .noWall),
		])
	}

	func testEdit_WhenLanesNotExist_ReturnsEmptyArray() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase(inserting: [])

		// Editing the lanes
		let lanes = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await LanesRepository.liveValue.edit(self.alleyId1)
		}

		// Returns an empty array
		XCTAssertEqual(lanes, [])
	}

	func testEdit_ReturnsLanesForAlley() async throws {
		// Given a database with two lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId2, id: id2, label: "2", position: .noWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2])

		// Editing the lanes
		let lanes = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await LanesRepository.liveValue.edit(self.alleyId1)
		}

		// Returns the lane for the alley
		XCTAssertEqual(lanes, [.init(id: id1, label: "1", position: .leftWall)])
	}

	// MARK: Create

	func testCreate_WhenLaneExists_ThrowsError() async throws {
		// Given a database with one lane
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let db = try await initializeDatabase(inserting: [lane1])

		// Creating the lane
		let created = Lane.Create(alleyId: alleyId1, id: id1, label: "2", position: .noWall)
		await assertThrowsError(ofType: DatabaseError.self) {
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await LanesRepository.liveValue.create([created])
			}
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Lane.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(updated?.id, id1)
		XCTAssertEqual(updated?.label, "1")
		XCTAssertEqual(updated?.position, .leftWall)
	}

	func testCreate_WhenLaneNotExists_CreatesLane() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase()

		let created = Lane.Create(alleyId: alleyId1, id: id1, label: "1", position: .noWall)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.create([created])
		}

		// Inserts one record
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Creates the lane
		let record = try await db.read { try Lane.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(record?.id, id1)
		XCTAssertEqual(record?.label, "1")
		XCTAssertEqual(record?.position, .noWall)
	}

	func testCreate_WhenMultipleLanes_CreatesAllLanes() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase()

		let created1 = Lane.Create(alleyId: alleyId1, id: id1, label: "1", position: .noWall)
		let created2 = Lane.Create(alleyId: alleyId1, id: id2, label: "2", position: .leftWall)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.create([created1, created2])
		}

		// Inserts two records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 2)

		// Creates the lanes
		let record1 = try await db.read { try Lane.Database.fetchOne($0, id: self.id1) }
		let record2 = try await db.read { try Lane.Database.fetchOne($0, id: self.id2) }
		XCTAssertEqual(record1?.id, id1)
		XCTAssertEqual(record2?.id, id2)
	}

	// MARK: Update

	func testUpdate_WhenLaneExists_UpdatesLane() async throws {
		// Given a database with one lane
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let db = try await initializeDatabase(inserting: [lane1])

		let edit = Lane.Edit(id: id1, label: "2", position: .noWall)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.update([edit])
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the lane
		let record = try await db.read { try Lane.Database.fetchOne($0, id: self.id1) }
		XCTAssertEqual(record?.id, id1)
		XCTAssertEqual(record?.label, "2")
		XCTAssertEqual(record?.position, .noWall)
	}

	func testUpdate_WhenLaneNotExists_ThrowsError() async throws {
		// Given a database with no lanes
		let db = try await initializeDatabase()

		// Updating the lane
		let edit = Lane.Edit(id: id1, label: "2", position: .noWall)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await LanesRepository.liveValue.update([edit])
			}
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testUpdate_WhenMultipleLanes_UpdatesAllLanes() async throws {
		// Given a database with two lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId1, id: id2, label: "2", position: .leftWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2])

		let edit1 = Lane.Edit(id: id1, label: "3", position: .noWall)
		let edit2 = Lane.Edit(id: id2, label: "4", position: .noWall)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.update([edit1, edit2])
		}

		// Does not insert any records
		let count = try await db.read { try Lane.Database.fetchCount($0) }
		XCTAssertEqual(count, 2)

		// Updates the lanes
		let record1 = try await db.read { try Lane.Database.fetchOne($0, id: self.id1) }
		let record2 = try await db.read { try Lane.Database.fetchOne($0, id: self.id2) }

		XCTAssertEqual(record1?.id, id1)
		XCTAssertEqual(record1?.label, "3")

		XCTAssertEqual(record2?.id, id2)
		XCTAssertEqual(record2?.label, "4")
	}

	// MARK: Delete

	func testDelete_WhenLaneExists_DeletesLane() async throws {
		// Given a database with 2 lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId1, id: id2, label: "2", position: .leftWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2])

		// Deleting the first lane
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.delete([self.id1])
		}

		// Updates the database
		let deletedExists = try await db.read { try Lane.Database.exists($0, id: self.id1) }
		XCTAssertFalse(deletedExists)

		// And leaves the other lane intact
		let otherExists = try await db.read { try Lane.Database.exists($0, id: self.id2) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenLaneNotExists_DoesNothing() async throws {
		// Given a database with 1 lane
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let db = try await initializeDatabase(inserting: [lane1])

		// Deleting a non-existent lane
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.delete([self.id2])
		}

		// Leaves the lane
		let exists = try await db.read { try Lane.Database.exists($0, id: self.id1) }
		XCTAssertTrue(exists)
	}

	func testDelete_WhenMultipleLanes_DeletesAllLanes() async throws {
		// Given a database with 3 lanes
		let lane1 = Lane.Database(alleyId: alleyId1, id: id1, label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: alleyId1, id: id2, label: "2", position: .leftWall)
		let lane3 = Lane.Database(alleyId: alleyId1, id: id3, label: "3", position: .leftWall)
		let db = try await initializeDatabase(inserting: [lane1, lane2, lane3])

		// Deleting two lanes
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await LanesRepository.liveValue.delete([self.id1, self.id2])
		}

		// Updates the database
		let deleted1Exists = try await db.read { try Lane.Database.exists($0, id: self.id1) }
		XCTAssertFalse(deleted1Exists)
		let deleted2Exists = try await db.read { try Lane.Database.exists($0, id: self.id2) }
		XCTAssertFalse(deleted2Exists)

		// And leaves the other lane intact
		let otherExists = try await db.read { try Lane.Database.exists($0, id: self.id3) }
		XCTAssertTrue(otherExists)
	}

	private func initializeDatabase(
		inserting lanes: [Lane.Database] = []
	) async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		migrator.registerDBMigrations()
		try migrator.migrate(dbQueue)

		let alleys = [
			Alley.Database(
				id: alleyId1,
				name: "Skyview",
				address: nil,
				material: nil,
				pinFall: nil,
				mechanism: nil,
				pinBase: nil
			),
			Alley.Database(
				id: alleyId2,
				name: "Grandview",
				address: nil,
				material: nil,
				pinFall: nil,
				mechanism: nil,
				pinBase: nil
			),
		]

		try await dbQueue.write {
			for alley in alleys {
				try alley.insert($0)
			}
			for lane in lanes {
				try lane.insert($0)
			}
		}

		return dbQueue
	}
}

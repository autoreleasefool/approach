import DatabaseModelsLibrary
import Dependencies
import GRDB
@testable import LocationsRepository
@testable import LocationsRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class LocationsRepositoryTests: XCTestCase {
	@Dependency(\.locations) var locations

	// MARK: - Create

	func testCreate_WhenLocationExists_ThrowsError() async throws {
		// Given a database with an existing location
		let location = Location.Database.mock(id: UUID(0))
		let db = try await initializeDatabase(withLocations: .custom([location]))

		// Creating the location
		let new = Location.Create(
			id: UUID(0),
			title: "456 Fake Street",
			coordinate: .init(latitude: 456, longitude: 456)
		)
		await assertThrowsError(ofType: DatabaseError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.locations = .liveValue
			} operation: {
				try await self.locations.create(new)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Location.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the existing location
		let existing = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(existing?.id, UUID(0))
		XCTAssertEqual(existing?.title, "123 Fake Street")
	}

	func testCreate_WhenLocationNotExists_CreatesLocation() async throws {
		// Given a database with no locations
		let db = try await initializeDatabase(withLocations: nil)

		// Creating the location
		let new = Location.Create(
			id: UUID(0),
			title: "456 Fake Street",
			coordinate: .init(latitude: 456, longitude: 456)
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.locations = .liveValue
		} operation: {
			try await self.locations.create(new)
		}

		// Inserts the location
		let count = try await db.read { try Location.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the database
		let existing = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(existing?.id, UUID(0))
		XCTAssertEqual(existing?.title, "456 Fake Street")
	}

	// MARK: - Edit

	func testEdit_WhenLocationExists_UpdatesLocation() async throws {
		// Given a database with an existing location
		let location = Location.Database.mock(id: UUID(0))
		let db = try await initializeDatabase(withLocations: .custom([location]))

		// Editing the location
		let existing = Location.Edit(
			id: UUID(0),
			title: "456 Fake Street",
			coordinate: .init(latitude: 456, longitude: 456)
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.locations = .liveValue
		} operation: {
			try await self.locations.edit(existing)
		}

		// Does not insert any records
		let count = try await db.read { try Location.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the database
		let updated = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.title, "456 Fake Street")
	}

	func testEdit_WhenLocationNotExists_ThrowsError() async throws {
		// Given a database with no locations
		let db = try await initializeDatabase(withLocations: nil)

		// Editing the location
		let existing = Location.Create(
			id: UUID(0),
			title: "456 Fake Street",
			coordinate: .init(latitude: 456, longitude: 456)
		)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.locations = .liveValue
			} operation: {
				try await self.locations.edit(existing)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Location.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}
}

extension Location.Database {
	static func mock(
		id: ID,
		title: String = "123 Fake Street",
		latitude: Double = 123,
		longitude: Double = 123
	) -> Self {
		.init(
			id: id,
			title: title,
			latitude: latitude,
			longitude: longitude
		)
	}
}


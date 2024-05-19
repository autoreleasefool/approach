import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import LocationsRepository
@testable import LocationsRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesPackageLibrary
import XCTest

final class LocationsRepositoryTests: XCTestCase {
	@Dependency(LocationsRepository.self) var locations

	// MARK: - InsertOrUpdate

	func testInsertOrUpdate_WhenLocationNotExists_CreatesLocation() async throws {
		// Given a database with no locations
		let db = try initializeDatabase(withLocations: nil)

		// Creating the location
		let new = Location.Create(
			id: UUID(0),
			title: "456 Fake Street",
			subtitle: "Viewgrand",
			coordinate: .init(latitude: 456, longitude: 456)
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[LocationsRepository.self] = .liveValue
		} operation: {
			try await self.locations.insertOrUpdate(new)
		}

		// Inserts the location
		let count = try await db.read { try Location.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the database
		let existing = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(existing?.id, UUID(0))
		XCTAssertEqual(existing?.title, "456 Fake Street")
	}

	func testInsertOrUpdate_WhenLocationExists_UpdatesLocation() async throws {
		// Given a database with an existing location
		let location = Location.Database.mock(id: UUID(0))
		let db = try initializeDatabase(withLocations: .custom([location]))

		// Editing the location
		let existing = Location.Edit(
			id: UUID(0),
			title: "456 Fake Street",
			subtitle: "Viewgrand",
			coordinate: .init(latitude: 456, longitude: 456)
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[LocationsRepository.self] = .liveValue
		} operation: {
			try await self.locations.insertOrUpdate(existing)
		}

		// Does not insert any records
		let count = try await db.read { try Location.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the database
		let updated = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.title, "456 Fake Street")
	}
}

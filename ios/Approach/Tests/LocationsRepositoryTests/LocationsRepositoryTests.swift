import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
@testable import LocationsRepository
@testable import LocationsRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("LocationsRepository", .tags(.repository))
struct LocationsRepositoryTests {

	@Suite("insertOrUpdate", .tags(.dependencies, .grdb))
	struct InsertOrUpdateTests {

		@Dependency(LocationsRepository.self) var locations

		@Test("Creates location when it does not exist", .tags(.unit))
		func createsLocationWhenItDoesNotExist() async throws {
			// Given a database with no locations
			let db = try initializeApproachDatabase(withLocations: nil)

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
				try await locations.insertOrUpdate(new)
			}

			// Inserts the location
			let count = try await db.read { try Location.Database.fetchCount($0) }
			#expect(count == 1)

			// Updates the database
			let existing = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
			#expect(existing?.id == UUID(0))
			#expect(existing?.title == "456 Fake Street")
		}

		@Test("Updates location when it exists", .tags(.unit))
		func updatesLocationWhenItExists() async throws {
			// Given a database with an existing location
			let location = Location.Database.mock(id: UUID(0))
			let db = try initializeApproachDatabase(withLocations: .custom([location]))

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
				try await locations.insertOrUpdate(existing)
			}

			// Does not insert any records
			let count = try await db.read { try Location.Database.fetchCount($0) }
			#expect(count == 1)

			// Updates the database
			let updated = try await db.read { try Location.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.title == "456 Fake Street")
		}
	}
}

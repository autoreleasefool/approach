@testable import DatabaseModelsLibrary
@testable import DatabaseService
import DatabaseServiceInterface
import Dependencies
import ExtensionsLibrary
import GRDB
import ModelsLibrary
import TestUtilitiesLibrary
import XCTest

final class AlleyDatabaseTests: XCTestCase {
	func test_AlleyWithPlaceholder_FailsToInsert() async throws {
		let db = try await initializeDatabase()

		let alley = Alley.Database(
			id: .placeholder,
			name: "Alley",
			address: nil,
			material: nil,
			pinFall: nil,
			mechanism: nil,
			pinBase: nil
		)

		await assertThrowsError(ofType: PlaceholderIDValidationError.self) {
			try await db.write {
				try alley.insert($0)
			}
		}

		let exists = try await db.read { try Alley.Database.exists($0, id: .placeholder) }
		XCTAssertFalse(exists)
	}

	private func initializeDatabase() async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)
		return dbQueue
	}
}

@testable import DatabaseModelsLibrary
@testable import DatabaseService
import DatabaseServiceInterface
import Dependencies
import ExtensionsLibrary
import ModelsLibrary
import GRDB
import TestUtilitiesLibrary
import XCTest

final class GearDatabaseTests: XCTestCase {
	func test_GearWithPlaceholder_FailsToInsert() async throws {
		let db = try await initializeDatabase()

		let gear = Gear.Database(
			id: .placeholder,
			name: "Name",
			kind: .bowlingBall,
			bowler: nil
		)

		await assertThrowsError(ofType: PlaceholderIDValidationError.self) {
			try await db.write {
				try gear.insert($0)
			}
		}

		let exists = try await db.read { try Gear.Database.exists($0, id: .placeholder) }
		XCTAssertFalse(exists)
	}

	private func initializeDatabase() async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)
		return dbQueue
	}
}

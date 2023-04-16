@testable import DatabaseModelsLibrary
@testable import DatabaseService
import DatabaseServiceInterface
import Dependencies
import ExtensionsLibrary
import ModelsLibrary
import GRDB
import TestUtilitiesLibrary
import XCTest

final class LaneDatabaseTests: XCTestCase {
	func test_LaneWithPlaceholder_FailsToInsert() async throws {
		let db = try await initializeDatabase()

		let lane = Lane.Database(
			alley: UUID(),
			id: .placeholder,
			label: "Lane",
			position: .leftWall
		)

		await assertThrowsError(ofType: PlaceholderIDValidationError.self) {
			try await db.write {
				try lane.insert($0)
			}
		}

		let exists = try await db.read { try Lane.Database.exists($0, id: .placeholder) }
		XCTAssertFalse(exists)
	}

	private func initializeDatabase() async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)
		return dbQueue
	}
}

@testable import DatabaseModelsLibrary
@testable import DatabaseService
import DatabaseServiceInterface
import Dependencies
import ExtensionsLibrary
import GRDB
import ModelsLibrary
import TestUtilitiesLibrary
import XCTest

final class LeagueDatabaseTests: XCTestCase {
	func test_LeagueWithPlaceholder_FailsToInsert() async throws {
		let db = try await initializeDatabase()

		let league = League.Database(
			bowlerId: UUID(),
			id: .placeholder,
			name: "League",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil,
			excludeFromStatistics: .include,
			alleyId: nil
		)

		await assertThrowsError(ofType: PlaceholderIDValidationError.self) {
			try await db.write {
				try league.insert($0)
			}
		}

		let exists = try await db.read { try League.Database.exists($0, id: .placeholder) }
		XCTAssertFalse(exists)
	}

	private func initializeDatabase() async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)
		return dbQueue
	}
}

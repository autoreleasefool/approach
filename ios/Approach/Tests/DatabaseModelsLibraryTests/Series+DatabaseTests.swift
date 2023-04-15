@testable import DatabaseModelsLibrary
@testable import DatabaseService
import DatabaseServiceInterface
import Dependencies
import ExtensionsLibrary
import ModelsLibrary
import GRDB
import TestUtilitiesLibrary
import XCTest

final class SeriesDatabaseTests: XCTestCase {
	func test_SeriesWithPlaceholder_FailsToInsert() async throws {
		let db = try await initializeDatabase()

		let series = Series.Database(
			league: UUID(),
			id: .placeholder,
			date: Date(),
			numberOfGames: 4,
			preBowl: .regular,
			excludeFromStatistics: .exclude,
			alley: nil
		)

		await assertThrowsError(ofType: PlaceholderIDValidationError.self) {
			try await db.write {
				try series.insert($0)
			}
		}

		let exists = try await db.read { try Series.Database.exists($0, id: .placeholder) }
		XCTAssertFalse(exists)
	}

	private func initializeDatabase() async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)
		return dbQueue
	}
}

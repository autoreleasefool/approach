import DatabaseMigrationsLibrary
import Dependencies
import FileManagerPackageServiceInterface
import Foundation
import GRDB
import ImportExportServiceInterface

actor IOSApproachSQLiteImporter: DataImporter {
	@Dependency(\.fileManager) var fileManager

	func startImport(of: URL, to: URL) async throws -> ImportResult {
		let dbQueue = try DatabaseQueue(path: of.path())

		let isSupported = try await isSupported(dbQueue: dbQueue)
		guard isSupported else { return .databaseTooNew }

		try fileManager.copyItem(at: of, to: to)
		return .success
	}

	private func isSupported(dbQueue: DatabaseQueue) async throws -> Bool {
		try await dbQueue.read {
			let rows = try Row.fetchCursor($0, sql: "SELECT * from grdb_migrations ORDER BY identifier DESC LIMIT 1")

			guard let row = try rows.next() else { return false }

			guard let latestMigrationIdentifier = row["identifier"] as? String else { return false }

			// As long as the last migration in the DB is one that we're aware of,
			// we will be able to successfully migrate the db when it is opened
			return Migrations.approachMigrations.map { $0.identifier }.contains(latestMigrationIdentifier)
		}
	}
}

import AnalyticsServiceInterface
import DatabaseLibrary
import DatabaseServiceInterface
import Dependencies
import ErrorReportingClientPackageLibrary
import FileManagerPackageServiceInterface
import GRDB

extension DatabaseService: DependencyKey {
	public static var liveValue: Self {
		@Dependency(\.fileManager) var fileManager
		let writer: any DatabaseWriter

		do {
			let folderUrl = try fileManager
				.getUserDirectory()
				.appending(path: "database", directoryHint: .isDirectory)

			try fileManager.createDirectory(folderUrl)

			let dbUrl = folderUrl.appending(path: "db.sqlite")
			let dbPool = try DatabasePool(path: dbUrl.path())
			writer = dbPool

			var migrator = DatabaseMigrator()
			migrator.registerDBMigrations()
			try migrator.migrate(writer)
		} catch {
			// FIXME: should notify user of failure to open DB
			@Dependency(\.errors) var errors
			errors.captureError(error)
			fatalError("Unable to access database service: \(error)")
		}

		return Self(
			reader: { writer },
			writer: { writer }
		)
	}
}

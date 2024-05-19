import AnalyticsServiceInterface
import DatabaseLibrary
import DatabaseServiceInterface
import Dependencies
import FileManagerPackageServiceInterface
import GRDB

extension DatabaseService: DependencyKey {
	public static var liveValue: Self {
		@Dependency(AnalyticsService.self) var analytics
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
			analytics.captureException(error)
			fatalError("Unable to access database service: \(error)")
		}

		return Self(
			reader: { writer },
			writer: { writer }
		)
	}
}

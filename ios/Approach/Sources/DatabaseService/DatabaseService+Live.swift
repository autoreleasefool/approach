import AnalyticsServiceInterface
import DatabaseLibrary
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FileManagerServiceInterface
import GRDB
import Harmony
import ModelsLibrary

extension DatabaseService: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(AnalyticsService.self) var analytics
		@Dependency(FileManagerService.self) var fileManager
//		let writer: any DatabaseWriter

		var migrator = DatabaseMigrator()
		migrator.registerDBMigrations()

		@Harmony(
			records: [
				Bowler.Database.self,
			],
			configuration: .init(),
			migrator: migrator
		) var harmony

//		do {
//			let folderUrl = try fileManager
//				.getUserDirectory()
//				.appending(path: "database", directoryHint: .isDirectory)
//
//			try fileManager.createDirectory(folderUrl)

//			let dbUrl = folderUrl.appending(path: "db.sqlite")
//			let dbPool = try DatabasePool(path: dbUrl.path())
//			writer = dbPool

//			var migrator = DatabaseMigrator()
//			migrator.registerDBMigrations()
//			try migrator.migrate(writer)
//		} catch {
//			// FIXME: should notify user of failure to open DB
//			analytics.captureException(error)
//			fatalError("Unable to access database service: \(error)")
//		}

		return Self(
			reader: { writer },
			writer: { writer }
		)
	}()
}

import Dependencies
import DatabaseServiceInterface
import FileManagerServiceInterface
import GRDB

extension DatabaseService: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.fileManagerService) var fileManager
		let writer: any DatabaseWriter

		do {
			let folderUrl = try fileManager
				.getUserDirectory()
				.appending(path: "database", directoryHint: .isDirectory)

			try fileManager.createDirectory(folderUrl)

			let dbUrl = folderUrl.appending(path: "db_next.sqlite")
			let dbPool = try DatabasePool(path: dbUrl.path())

			var migrator = DatabaseMigrator()

			#if DEBUG
			migrator.eraseDatabaseOnSchemaChange = true
			#endif

			migrator.registerMigration(Migration20230325CreateBowler.self)

			writer = dbPool
			try migrator.migrate(writer)
		} catch {
			// TODO: should notify user of failure to open DB
			fatalError("Unable to access database service: \(error)")
		}

		return Self(
			reader: { writer },
			writer: { writer }
		)
	}()
}

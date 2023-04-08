import DatabaseServiceInterface
import Dependencies
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
			writer = dbPool

			var migrator = DatabaseMigrator()
			try migrator.prepare(writer)
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

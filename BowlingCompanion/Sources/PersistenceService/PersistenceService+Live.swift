import Dependencies
import FileManagerServiceInterface
import Foundation
import GRDB
import PersistenceServiceInterface

extension PersistenceService: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.fileManagerService) var fileManagerService

		let appDb: AppDatabase
		do {
			let folderUrl = try fileManagerService.getUserDirectory()
				.appending(path: "database", directoryHint: .isDirectory)

			try fileManagerService.createDirectory(folderUrl)

			let dbUrl = folderUrl.appending(path: "db.sqlite")
			let dbPool = try DatabasePool(path: dbUrl.path())

			appDb = try AppDatabase(dbPool)
		} catch {
			// TODO: should notify user of failure to open DB
			fatalError("Unable to access persistence service, \(error)")
		}

		return Self(
			reader: {
				appDb.dbReader
			},
			write: { block in
				try await block(appDb.dbWriter)
			}
		)
	}()
}

struct AppDatabase {
	let dbWriter: any DatabaseWriter

	var dbReader: DatabaseReader {
		dbWriter
	}

	init(_ dbWriter: any DatabaseWriter) throws {
		self.dbWriter = dbWriter
		try migrator.migrate(dbWriter)
	}

	private var migrator: DatabaseMigrator {
		var migrator = DatabaseMigrator()

		#if DEBUG
		migrator.eraseDatabaseOnSchemaChange = true
		#endif

		migrator.registerMigration(Migration20221018CreateBowler.self)
		migrator.registerMigration(Migration20221021CreateLeague.self)
		migrator.registerMigration(Migration20221021CreateSeries.self)
		migrator.registerMigration(Migration20221021CreateGame.self)
		migrator.registerMigration(Migration20221021CreateFrame.self)
		migrator.registerMigration(Migration20221101CreateAlley.self)

		return migrator
	}
}

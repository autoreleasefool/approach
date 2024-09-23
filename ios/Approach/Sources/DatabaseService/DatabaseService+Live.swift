import DatabaseMigrationsLibrary
import DatabaseServiceInterface
import Dependencies
import FileManagerPackageServiceInterface
import Foundation
import GRDB
import GRDBDatabasePackageLibrary
import GRDBDatabasePackageServiceInterface

extension DatabaseService: DependencyKey {
	public static var liveValue: Self {
#if DEBUG
		let eraseDatabaseSchemaOnChange = true
#else
		let eraseDatabaseSchemaOnChange = false
#endif

		@Dependency(\.fileManager) var fileManager
		let dbUrl: URL
		do {
			let folderUrl = try fileManager
				.getUserDirectory()
				.appending(path: "database", directoryHint: .isDirectory)

			try fileManager.createDirectory(folderUrl)

			dbUrl = folderUrl.appending(path: "db.sqlite")
		} catch {
			// FIXME: should notify user of failure to open DB
			fatalError("Unable to access database path: \(error)")
		}

		return Self(
			initialize: {
				@Dependency(\.grdb) var grdb
				do {
					try grdb.initialize(
						dbUrl: dbUrl,
						migrations: Migrations.approachMigrations,
						eraseDatabaseOnSchemaChange: eraseDatabaseSchemaOnChange
					)
				} catch {
					// FIXME: should notify user of failure to open DB
					fatalError("Unable to initialize database: \(error)")
				}
			},
			dbUrl: { dbUrl },
			close: {
				@Dependency(\.grdb) var grdb
				try grdb.close()
			},
			reader: {
				@Dependency(\.grdb) var grdb
				// swiftlint:disable:next force_try
				return try! grdb.reader()
			},
			writer: {
				@Dependency(\.grdb) var grdb
				// swiftlint:disable:next force_try
				return try! grdb.writer()
			}
		)
	}
}

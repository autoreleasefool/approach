import DatabaseMigrationsLibrary
import DatabaseServiceInterface
import Dependencies
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

		@Dependency(\.grdb) var grdb
		do {
			try grdb.initialize(
				migrations: Migrations.approachMigrations,
				eraseDatabaseOnSchemaChange: eraseDatabaseSchemaOnChange
			)
		} catch {
			// FIXME: should notify user of failure to open DB
			fatalError("Unable to initialize database: \(error)")
		}

		return Self(
			reader: {
				@Dependency(\.grdb) var grdb
				return grdb.reader()
			},
			writer: {
				@Dependency(\.grdb) var grdb
				return grdb.writer()
			}
		)
	}
}

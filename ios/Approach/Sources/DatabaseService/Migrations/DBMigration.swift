import GRDB

protocol DBMigration {
	static var identifier: String { get }
	static func migrate(_ db: Database) throws
}

extension DBMigration {
	static var identifier: String { "\(Self.self)" }
}

extension DatabaseMigrator {
	mutating func prepare(_ writer: any DatabaseWriter) throws {
		#if DEBUG
		eraseDatabaseOnSchemaChange = true
		#endif

		registerMigration(Migration20230325CreateBowler.self)
		registerMigration(Migration20230408CreateAlley.self)
		registerMigration(Migration20230408CreateLeague.self)
		registerMigration(Migration20230409CreateSeries.self)

		try migrate(writer)
	}

	mutating func registerMigration(_ migration: DBMigration.Type) {
		self.registerMigration(migration.identifier, migrate: migration.migrate(_:))
	}
}

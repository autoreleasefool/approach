import GRDB

protocol DBMigration {
	static var identifier: String { get }
	static func migrate(_ db: Database) throws
}

extension DBMigration {
	static var identifier: String { "\(Self.self)" }
}

extension DatabaseMigrator {
	public mutating func registerDBMigrations() {
		#if DEBUG
		eraseDatabaseOnSchemaChange = true
		#endif

		registerMigration(Migration20230325CreateBowler.self)
		registerMigration(Migration20230408CreateAlley.self)
		registerMigration(Migration20230408CreateLeague.self)
		registerMigration(Migration20230409CreateSeries.self)
		registerMigration(Migration20230413CreateGear.self)
		registerMigration(Migration20230414CreateGame.self)
		registerMigration(Migration20230414CreateFrame.self)
		registerMigration(Migration20230415CreateLane.self)
		registerMigration(Migration20230417CreateSeriesLanePivot.self)
		registerMigration(Migration20230425CreateAvatar.self)
		registerMigration(Migration20230426CreateLocation.self)
	}

	mutating func registerMigration(_ migration: DBMigration.Type) {
		self.registerMigration(migration.identifier, migrate: migration.migrate(_:))
	}
}

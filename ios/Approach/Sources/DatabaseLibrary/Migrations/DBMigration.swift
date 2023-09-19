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
		registerMigration(Migration20230506AddGearToFrame.self)
		registerMigration(Migration20230514CreateMatchPlay.self)
		registerMigration(Migration20230519AddScoreToGame.self)
		registerMigration(Migration20230531GameLanes.self)
		registerMigration(Migration20230602AddGearToGame.self)
		registerMigration(Migration20230630CreateStatisticsWidget.self)
		registerMigration(Migration20230912CreateBowlerPreferredGear.self)
		registerMigration(Migration20230913AddAvatarToGear.self)
		registerMigration(Migration20230918ChangeWidgetSourceToColumns.self)
		registerMigration(Migration20230918OnDeleteGearBowlerSetNull.self)
	}

	mutating func registerMigration(_ migration: DBMigration.Type) {
		self.registerMigration(migration.identifier, migrate: migration.migrate(_:))
	}
}

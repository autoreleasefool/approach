@testable import DatabaseMigrationsLibrary
@testable import DatabaseService
@testable import DatabaseServiceInterface
import GRDB
import Testing
import TestUtilitiesLibrary

@Suite("DBMigration Tests", .tags(.grdb))
struct DBMigrationTests {
	static let allMigrations = [
		"Migration20230325CreateBowler",
		"Migration20230408CreateAlley",
		"Migration20230408CreateLeague",
		"Migration20230409CreateSeries",
		"Migration20230413CreateGear",
		"Migration20230414CreateGame",
		"Migration20230414CreateFrame",
		"Migration20230415CreateLane",
		"Migration20230417CreateSeriesLanePivot",
		"Migration20230425CreateAvatar",
		"Migration20230426CreateLocation",
		"Migration20230506AddGearToFrame",
		"Migration20230514CreateMatchPlay",
		"Migration20230519AddScoreToGame",
		"Migration20230531GameLanes",
		"Migration20230602AddGearToGame",
		"Migration20230630CreateStatisticsWidget",
		"Migration20230912CreateBowlerPreferredGear",
		"Migration20230913AddAvatarToGear",
		"Migration20230918ChangeWidgetSourceToColumns",
		"Migration20230918OnDeleteGearBowlerSetNull",
		"Migration20230918MigrateStatisticsWidgetType",
		"Migration20230920ValidateGameScores",
		"Migration20231002ValidateGameScores",
		"Migration20231022IsArchivedProperty",
		"Migration20231022DropSeriesNumberOfGames",
		"Migration20231023AddArchivePropertyToGame",
		"Migration20231024IsArchivedToArchivedOn",
		"Migration20231128AddDurationToGame",
		"Migration20231220MigrateRecurringLeagues",
		"Migration20231220RenameLeagueNumberOfGames",
		"Migration20240322AddBowledOnDateToSeries",
		"Migration20240329ClearExtraRolledBalls",
		"Migration20241021CreateTeam",
	]

	@Test("identifier matches")
	func identifierMatches() {
		#expect(Migration20230325CreateBowler.identifier == "Migration20230325CreateBowler")
	}

	@Test("Registers migrations")
	func registersMigrations() throws {
		var migrator = DatabaseMigrator()
		#expect(migrator.migrations.isEmpty)

		migrator.register(migration: Migration20230325CreateBowler.self)
		#expect(migrator.migrations == ["Migration20230325CreateBowler"])
	}

	@Test("Runs migrations")
	func runsMigrations() throws {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()

		let appliedBefore = try dbQueue.read {
			try migrator.appliedMigrations($0)
		}
		#expect(appliedBefore.isEmpty)

		for migration in Migrations.approachMigrations {
			migrator.register(migration: migration)
		}
		try migrator.migrate(dbQueue)

		let appliedAfter = try dbQueue.read {
			try migrator.appliedMigrations($0)
		}
		#expect(appliedAfter == Self.allMigrations)
	}
}

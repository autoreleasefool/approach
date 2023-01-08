import Dependencies
import FileManagerServiceInterface
import GRDB

struct DatabaseManager {
	let writer: any DatabaseWriter
	var reader: DatabaseReader { writer }

	init() throws {
		@Dependency(\.fileManagerService) var fileManagerService

		let folderUrl = try fileManagerService.getUserDirectory()
			.appending(path: "database", directoryHint: .isDirectory)

		try fileManagerService.createDirectory(folderUrl)

		let dbUrl = folderUrl.appending(path: "db.sqlite")
		let dbPool = try DatabasePool(path: dbUrl.path())

		self.writer = dbPool
		try migrator.migrate(writer)
	}

	private var migrator: DatabaseMigrator {
		var migrator = DatabaseMigrator()

		#if DEBUG
		migrator.eraseDatabaseOnSchemaChange = true
		#endif

		migrator.registerMigration(Migration20221101CreateAlley.self)
		migrator.registerMigration(Migration20221018CreateBowler.self)
		migrator.registerMigration(Migration20221115CreateGear.self)
		migrator.registerMigration(Migration20221021CreateLeague.self)
		migrator.registerMigration(Migration20221021CreateSeries.self)
		migrator.registerMigration(Migration20221021CreateGame.self)
		migrator.registerMigration(Migration20221021CreateFrame.self)
		migrator.registerMigration(Migration20221204CreateLane.self)
		migrator.registerMigration(Migration20221204CreateGameLanePivot.self)
		migrator.registerMigration(Migration20230103CreateTeam.self)
		migrator.registerMigration(Migration20230103CreateTeamMemberPivot.self)

		return migrator
	}
}

import GRDB

struct Migration20231220RenameLeagueNumberOfGames: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "league") { t in
			t.rename(column: "numberOfGames", to: "defaultNumberOfGames")
		}
	}
}

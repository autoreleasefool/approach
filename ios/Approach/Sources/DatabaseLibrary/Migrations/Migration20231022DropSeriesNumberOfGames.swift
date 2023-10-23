import GRDB

struct Migration20231022DropSeriesNumberOfGames: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "series") { t in
			t.drop(column: "numberOfGames")
		}
	}
}

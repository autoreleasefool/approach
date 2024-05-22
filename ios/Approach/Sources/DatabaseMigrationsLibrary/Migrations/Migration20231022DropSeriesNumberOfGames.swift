import GRDB
import GRDBDatabasePackageLibrary

struct Migration20231022DropSeriesNumberOfGames: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "series") { t in
			t.drop(column: "numberOfGames")
		}
	}
}

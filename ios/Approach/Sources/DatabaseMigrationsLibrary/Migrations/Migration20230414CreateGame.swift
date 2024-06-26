import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230414CreateGame: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "game") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("seriesId", .text)
				.notNull()
				.indexed()
				.references("series", onDelete: .cascade)
			t.column("index", .integer)
				.notNull()
			t.column("locked", .text)
				.notNull()
			t.column("manualScore", .integer)
			t.column("excludeFromStatistics", .text)
				.notNull()
		}
	}
}

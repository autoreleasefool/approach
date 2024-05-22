import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230408CreateLeague: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "league") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("bowlerId", .text)
				.notNull()
				.indexed()
				.references("bowler", onDelete: .cascade)
			t.column("name", .text)
				.notNull()
			t.column("recurrence", .text)
				.notNull()
			t.column("numberOfGames", .integer)
			t.column("additionalPinfall", .integer)
			t.column("additionalGames", .integer)
			t.column("excludeFromStatistics", .text)
				.notNull()
			t.column("alleyId", .text)
				.indexed()
				.references("alley", onDelete: .setNull)
		}
	}
}

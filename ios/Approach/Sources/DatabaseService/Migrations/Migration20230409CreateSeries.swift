import GRDB

struct Migration20230409CreateSeries: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "series") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("league", .text)
				.notNull()
				.indexed()
				.references("league", onDelete: .cascade)
			t.column("date", .datetime)
				.notNull()
			t.column("numberOfGames", .integer)
				.notNull()
			t.column("preBowl", .integer)
				.notNull()
			t.column("excludeFromStatistics", .integer)
				.notNull()
			t.column("alley", .text)
				.indexed()
				.references("alley", onDelete: .setNull)
		}
	}
}

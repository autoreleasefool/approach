import GRDB

struct Migration20221021CreateLeague: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "league") { t in
			t.column("id", .text).primaryKey()
			t.column("bowlerId", .text).references("bowler", onDelete: .cascade)
			t.column("name", .text).notNull()
			t.column("recurrence", .text).notNull()
			t.column("numberOfGames", .integer).notNull()
			t.column("additionalPinfall", .integer)
			t.column("additionalGames", .integer)
			t.column("createdAt", .datetime).notNull()
			t.column("lastModifiedAt", .datetime).notNull()
		}
	}
}

import GRDB

struct Migration20221021CreateGame: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "game") { t in
			t.column("id", .text).primaryKey()
			t.column("seriesId", .text).references("series", onDelete: .cascade)
			t.column("ordinal", .integer).notNull()
			t.column("locked", .text).notNull()
			t.column("manualScore", .integer)
		}
	}
}

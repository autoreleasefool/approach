import GRDB

struct Migration20221115CreateGear: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "gear") { t in
			t.column("id", .text).primaryKey()
			t.column("bowlerId", .text).references("bowler", onDelete: .cascade)
			t.column("name", .text).notNull()
			t.column("kind", .text).notNull()
		}
	}
}

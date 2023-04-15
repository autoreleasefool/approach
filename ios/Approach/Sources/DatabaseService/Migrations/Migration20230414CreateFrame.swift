import GRDB

struct Migration20230414CreateFrame: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "frame") { t in
			t.column("game", .text)
				.notNull()
				.indexed()
				.references("game", onDelete: .cascade)
			t.column("ordinal", .integer)
				.notNull()
			t.column("roll0", .text)
			t.column("roll1", .text)
			t.column("roll2", .text)

			t.primaryKey(["game", "ordinal"])
		}
	}
}

import GRDB

struct Migration20230415CreateLane: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "lane") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("label", .text)
				.notNull()
			t.column("position", .text)
				.notNull()
			t.column("alleyId", .text)
				.notNull()
				.indexed()
				.references("alley", onDelete: .cascade)
		}
	}
}

import GRDB

struct Migration20230325CreateBowler: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "bowler") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
		}
	}
}

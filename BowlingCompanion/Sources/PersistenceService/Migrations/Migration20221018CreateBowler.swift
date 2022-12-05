import GRDB

struct Migration20221018CreateBowler: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "bowler") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
		}
	}
}

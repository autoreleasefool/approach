import GRDB

struct Migration20230108CreateOpponent: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "opponent") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
		}
	}
}

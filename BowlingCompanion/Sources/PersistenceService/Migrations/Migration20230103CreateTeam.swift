import GRDB

struct Migration20230103CreateTeam: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "team") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
		}
	}
}

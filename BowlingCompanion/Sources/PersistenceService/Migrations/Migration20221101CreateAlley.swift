import GRDB

struct Migration20221101CreateAlley: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "alley") { t in
			t.column("id", .text).primaryKey()
			t.column("name", .text).notNull()
		}
	}
}

import GRDB

struct Migration20230425CreateAvatar: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "avatar") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("value", .blob)
				.notNull()
		}
	}
}

import GRDB

struct Migration20221204CreateLane: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "lane") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("label", .text)
				.notNull()
			t.column("isAgainstWall", .boolean)
				.notNull()
			t.column("alley", .text)
				.notNull()
				.indexed()
				.references("alley", onDelete: .cascade)
		}
	}
}

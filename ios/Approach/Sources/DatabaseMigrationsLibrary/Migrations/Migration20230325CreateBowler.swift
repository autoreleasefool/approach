import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230325CreateBowler: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "bowler") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
			t.column("kind", .text)
				.notNull()
		}
	}
}

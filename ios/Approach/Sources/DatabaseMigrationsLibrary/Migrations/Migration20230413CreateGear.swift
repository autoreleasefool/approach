import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230413CreateGear: Migration {
	static func migrate(_ db: Database) throws {
		// NOTE: Superceded in Migration20230918OnDeleteGearBowlerSetNull
		try db.create(table: "gear") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("bowlerId", .text)
				.indexed()
				.references("bowler", onDelete: .cascade)
			t.column("name", .text)
				.notNull()
			t.column("kind", .text)
				.notNull()
		}
	}
}

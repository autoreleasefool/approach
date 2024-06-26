import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230414CreateFrame: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "frame") { t in
			t.column("gameId", .text)
				.notNull()
				.indexed()
				.references("game", onDelete: .cascade)
			t.column("index", .integer)
				.notNull()
			t.column("roll0", .text)
			t.column("roll1", .text)
			t.column("roll2", .text)

			t.primaryKey(["gameId", "index"])
		}
	}
}

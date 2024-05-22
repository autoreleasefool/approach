import GRDB
import GRDBDatabasePackageLibrary

struct Migration20231128AddDurationToGame: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "game") { t in
			t.add(column: "duration", .double)
				.notNull()
				.defaults(to: 0)
		}
	}
}

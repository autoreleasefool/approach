import GRDB
import GRDBDatabasePackageLibrary

struct Migration20231022IsArchivedProperty: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "bowler") { t in
			t.add(column: "isArchived", .boolean)
				.defaults(to: false)
		}

		try db.alter(table: "league") { t in
			t.add(column: "isArchived", .boolean)
				.defaults(to: false)
		}

		try db.alter(table: "series") { t in
			t.add(column: "isArchived", .boolean)
				.defaults(to: false)
		}
	}
}

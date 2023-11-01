import GRDB

struct Migration20231024IsArchivedToArchivedOn: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "bowler") { t in
			t.drop(column: "isArchived")
			t.add(column: "archivedOn", .datetime)
		}

		try db.alter(table: "league") { t in
			t.drop(column: "isArchived")
			t.add(column: "archivedOn", .datetime)
		}

		try db.alter(table: "series") { t in
			t.drop(column: "isArchived")
			t.add(column: "archivedOn", .datetime)
		}

		try db.alter(table: "game") { t in
			t.drop(column: "isArchived")
			t.add(column: "archivedOn", .datetime)
		}
	}
}

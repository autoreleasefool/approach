import GRDB

// swiftlint:disable:next type_name
struct Migration20240406AddHarmony: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "alley") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "avatar") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "bowler") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "bowlerPreferredGear") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "frame") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "game") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "gameGear") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "gameLane") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "gear") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "lane") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "league") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "location") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "mtchPlay") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "series") { t in
			t.add(column: "archivedRecordData", .blob)
		}

		try db.alter(table: "statisticsWidget") { t in
			t.add(column: "archivedRecordData", .blob)
		}
	}
}

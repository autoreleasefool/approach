import GRDB

struct Migration20230630CreateStatisticsWidget: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "statisticsWidget") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("created", .date)
				.notNull()
			t.column("source", .text)
				.notNull()
			t.column("timeline", .text)
				.notNull()
			t.column("statistic", .text)
				.notNull()
			t.column("context", .text)
				.notNull()
			t.column("priority", .integer)
				.notNull()
		}
	}
}

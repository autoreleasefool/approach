import GRDB

struct Migration20240322AddBowledOnDateToSeries: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "series") { t in
			t.add(column: "appliedDate", .datetime)
		}
	}
}

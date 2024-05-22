import GRDB
import GRDBDatabasePackageLibrary

struct Migration20240322AddBowledOnDateToSeries: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "series") { t in
			t.add(column: "appliedDate", .datetime)
		}
	}
}

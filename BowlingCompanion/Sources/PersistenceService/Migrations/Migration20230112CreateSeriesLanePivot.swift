import GRDB

struct Migration20230112CreateSeriesLanePivot: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "seriesLane") { t in
			t.column("series", .text)
				.notNull()
				.indexed()
				.references("series", onDelete: .cascade)
			t.column("lane", .text)
				.notNull()
				.indexed()
				.references("lane", onDelete: .cascade)

			t.primaryKey(["series", "lane"])
		}
	}
}

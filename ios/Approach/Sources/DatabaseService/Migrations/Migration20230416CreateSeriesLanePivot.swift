import GRDB

struct Migration20230417CreateSeriesLanePivot: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "seriesLane") { t in
			t.column("seriesId", .text)
				.notNull()
				.indexed()
				.references("series", onDelete: .cascade)
			t.column("laneId", .text)
				.notNull()
				.indexed()
				.references("lane", onDelete: .cascade)

			t.primaryKey(["seriesId", "laneId"])
		}
	}
}

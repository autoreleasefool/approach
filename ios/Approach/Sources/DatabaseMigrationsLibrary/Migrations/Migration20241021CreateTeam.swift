import GRDB
import GRDBDatabasePackageLibrary

struct Migration20241021CreateTeam: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "team") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
		}

		try db.create(table: "teamSeries") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("teamId", .text)
				.indexed()
				.references("team", onDelete: .cascade)
			t.column("date", .datetime)
				.notNull()
			t.column("archivedOn", .datetime)
		}

		try db.create(table: "teamBowler") { t in
			t.column("teamId", .text)
				.notNull()
				.indexed()
				.references("team", onDelete: .cascade)
			t.column("bowlerId", .text)
				.notNull()
				.indexed()
				.references("bowler", onDelete: .cascade)
			t.column("position", .integer)
				.notNull()
				.defaults(to: 0)

			t.primaryKey(["teamId", "bowlerId"])
		}

		try db.create(table: "teamSeriesSeries") { t in
			t.column("teamSeriesId", .text)
				.notNull()
				.indexed()
				.references("teamSeries", onDelete: .cascade)
			t.column("seriesId", .text)
				.notNull()
				.indexed()
				.references("series", onDelete: .cascade)
			t.column("position", .integer)
				.notNull()

			t.primaryKey(["teamSeriesId", "seriesId"])
		}
	}
}

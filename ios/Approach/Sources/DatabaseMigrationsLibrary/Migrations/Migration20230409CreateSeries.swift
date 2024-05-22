import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230409CreateSeries: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "series") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("leagueId", .text)
				.notNull()
				.indexed()
				.references("league", onDelete: .cascade)
			t.column("date", .datetime)
				.notNull()
			t.column("numberOfGames", .integer)
				.notNull()
			t.column("preBowl", .integer)
				.notNull()
			t.column("excludeFromStatistics", .integer)
				.notNull()
			t.column("alleyId", .text)
				.indexed()
				.references("alley", onDelete: .setNull)
		}
	}
}

import GRDB

struct Migration20221021CreateSeries: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "series") { t in
			t.column("id", .text).primaryKey()
			t.column("leagueId", .text).references("league", onDelete: .cascade)
			t.column("date", .datetime).notNull()
			t.column("numberOfGames", .integer).notNull()
		}
	}
}

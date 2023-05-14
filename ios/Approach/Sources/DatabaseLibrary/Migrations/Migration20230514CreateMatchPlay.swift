import GRDB

struct Migration20230514CreateMatchPlay: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "matchPlay") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("gameId", .text)
				.notNull()
				.indexed()
				.references("game")
			t.column("opponent", .text)
				.notNull()
				.indexed()
				.references("bowler")
			t.column("opponentScore", .integer)
			t.column("result", .text)
		}
	}
}

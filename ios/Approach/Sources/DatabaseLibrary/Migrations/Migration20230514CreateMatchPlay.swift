import GRDB

struct Migration20230514CreateMatchPlay: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "matchPlay") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("gameId", .text)
				.notNull()
				.indexed()
				.references("game", onDelete: .cascade)
			t.column("opponentId", .text)
				.indexed()
				.references("bowler", onDelete: .setNull)
			t.column("opponentScore", .integer)
			t.column("result", .text)
		}
	}
}

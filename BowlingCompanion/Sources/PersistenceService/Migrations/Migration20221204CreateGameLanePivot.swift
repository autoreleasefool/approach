import GRDB

struct Migration20221204CreateGameLanePivot: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "gameLanes") { t in
			t.column("game", .text)
				.notNull()
				.indexed()
				.references("game", onDelete: .cascade)
			t.column("lane", .text)
				.notNull()
				.indexed()
				.references("lane", onDelete: .cascade)
		}
	}
}

import GRDB

struct Migration20221021CreateFrame: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "frame") { t in
			t.column("gameId", .text).notNull().references("game", onDelete: .cascade)
			t.column("ordinal", .integer).notNull()
			t.column("firstBall", .text)
			t.column("secondBall", .text)
			t.column("thirdBall", .text)

			t.primaryKey(["gameId", "ordinal"])
		}
	}
}

import GRDB

struct Migration20230531GameLanes: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.drop(index: "league_on_alleyId")

		try db.alter(table: "league") { t in
			t.drop(column: "alleyId")
		}

		try db.drop(table: "seriesLane")

		try db.create(table: "gameLane") { t in
			t.column("gameId", .text)
				.notNull()
				.indexed()
				.references("game", onDelete: .cascade)
			t.column("laneId", .text)
				.notNull()
				.indexed()
				.references("lane", onDelete: .cascade)

			t.primaryKey(["gameId", "laneId"])
		}
	}
}

import GRDB

struct Migration20230103CreateTeamBowlerPivot: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "teamBowlers") { t in
			t.column("team", .text)
				.notNull()
				.indexed()
				.references("team", onDelete: .cascade)
			t.column("bowler", .text)
				.notNull()
				.indexed()
				.references("bowler", onDelete: .cascade)

			t.primaryKey(["team", "bowler"])
		}
	}
}

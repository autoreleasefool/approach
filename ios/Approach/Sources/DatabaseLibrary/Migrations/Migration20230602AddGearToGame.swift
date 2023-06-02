import GRDB

struct Migration20230602AddGearToGame: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "gameGear") { t in
			t.column("gameId", .text)
				.notNull()
				.indexed()
				.references("game", onDelete: .cascade)
			t.column("gearId", .text)
				.notNull()
				.indexed()
				.references("gear", onDelete: .cascade)

			t.primaryKey(["gameId", "gearId"])
		}
	}
}

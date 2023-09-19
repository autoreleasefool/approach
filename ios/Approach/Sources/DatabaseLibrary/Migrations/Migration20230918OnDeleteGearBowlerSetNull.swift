import GRDB

// swiftlint:disable:next type_name
struct Migration20230918OnDeleteGearBowlerSetNull: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "new_gear") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("bowlerId", .text)
				.indexed()
				.references("bowler", onDelete: .setNull)
			t.column("name", .text)
				.notNull()
			t.column("kind", .text)
				.notNull()
			t.column("avatarId", .text)
				.indexed()
				.notNull()
				.references("avatar", onDelete: .cascade)
		}

		try db.execute(sql: "INSERT INTO new_gear SELECT * from gear")

		try db.drop(table: "gear")
		try db.rename(table: "new_gear", to: "gear")
	}
}

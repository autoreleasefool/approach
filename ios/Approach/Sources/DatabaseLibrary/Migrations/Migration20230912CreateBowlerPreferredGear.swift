import GRDB

// swiftlint:disable:next type_name
struct Migration20230912CreateBowlerPreferredGear: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "bowlerPreferredGear") { t in
			t.column("bowlerId", .text)
				.notNull()
				.indexed()
				.references("bowler", onDelete: .cascade)
			t.column("gearId", .text)
				.notNull()
				.indexed()
				.references("gear", onDelete: .cascade)

			t.primaryKey(["bowlerId", "gearId"])
		}
	}
}

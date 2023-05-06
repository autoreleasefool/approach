import GRDB

struct Migration20230506AddGearToFrame: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "frame") { t in
			t.add(column: "ball0", .text)
				.indexed()
				.references("gear", onDelete: .setNull)

			t.add(column: "ball1", .text)
				.indexed()
				.references("gear", onDelete: .setNull)

			t.add(column: "ball2", .text)
				.indexed()
				.references("gear", onDelete: .setNull)
		}
	}
}

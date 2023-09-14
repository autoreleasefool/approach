import GRDB

struct Migration20230913AddAvatarToGear: DBMigration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "gear") { t in
			t.add(column: "avatarId", .text)
				.indexed()
				.references("avatar", onDelete: .cascade)
		}
	}
}

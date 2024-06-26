import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230913AddAvatarToGear: Migration {
	static func migrate(_ db: Database) throws {
		// NOTE: Superceded in Migration20230918OnDeleteGearBowlerSetNull
		try db.alter(table: "gear") { t in
			t.add(column: "avatarId", .text)
				.indexed()
				.references("avatar", onDelete: .cascade)
		}
	}
}

import GRDB

struct Migration20230228AddAvatarToBowler: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "bowler") { t in
			t.add(column: "avatar", .blob)
		}
	}
}

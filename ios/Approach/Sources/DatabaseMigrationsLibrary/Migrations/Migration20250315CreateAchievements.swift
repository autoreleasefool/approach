import GRDB
import GRDBDatabasePackageLibrary

struct Migration20250315CreateAchievements: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "achievement") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("title", .text)
				.notNull()
			t.column("earnedAt", .datetime)
				.notNull()
		}

		try db.create(table: "achievementEvent") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("title", .text)
				.notNull()
			t.column("isConsumed", .boolean)
				.defaults(to: false)
				.notNull()
		}
	}
}

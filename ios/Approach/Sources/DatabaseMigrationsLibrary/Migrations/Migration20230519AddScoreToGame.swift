import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230519AddScoreToGame: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "game") { t in
			t.drop(column: "manualScore")
			t.add(column: "score", .integer)
				.notNull()
			t.add(column: "scoringMethod", .text)
				.notNull()
		}
	}
}

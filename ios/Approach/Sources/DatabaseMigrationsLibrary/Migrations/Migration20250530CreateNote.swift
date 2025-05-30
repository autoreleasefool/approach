import GRDB
import GRDBDatabasePackageLibrary

struct Migration20250530CreateNote: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "note") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("entityType", .text)
			t.column("entityId", .text)
			t.column("content", .text)
		}

		try db.create(indexOn: "note", columns: [
			"entityType",
			"entityId"
		])
	}
}

import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230426CreateLocation: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "location") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("title", .text)
				.notNull()
			t.column("subtitle", .text)
			t.column("latitude", .double)
				.notNull()
			t.column("longitude", .double)
				.notNull()
		}

		try db.alter(table: "alley") { t in
			t.drop(column: "address")
			t.add(column: "locationId", .text)
				.indexed()
				.references("location", onDelete: .cascade)
		}
	}
}

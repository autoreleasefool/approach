import GRDB
import GRDBDatabasePackageLibrary

struct Migration20230408CreateAlley: Migration {
	static func migrate(_ db: Database) throws {
		try db.create(table: "alley") { t in
			t.column("id", .text)
				.primaryKey()
			t.column("name", .text)
				.notNull()
			t.column("address", .text)
			t.column("material", .text)
			t.column("pinFall", .text)
			t.column("mechanism", .text)
			t.column("pinBase", .text)
		}
	}
}

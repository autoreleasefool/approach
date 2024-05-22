import GRDB
import GRDBDatabasePackageLibrary

// swiftlint:disable:next type_name
struct Migration20231023AddArchivePropertyToGame: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "game") { t in
			t.add(column: "isArchived", .boolean)
				.defaults(to: false)
		}
	}
}

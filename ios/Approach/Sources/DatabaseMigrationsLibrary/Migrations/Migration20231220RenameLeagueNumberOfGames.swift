import GRDB
import GRDBDatabasePackageLibrary

// swiftlint:disable:next type_name
struct Migration20231220RenameLeagueNumberOfGames: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "league") { t in
			t.rename(column: "numberOfGames", to: "defaultNumberOfGames")
		}
	}
}

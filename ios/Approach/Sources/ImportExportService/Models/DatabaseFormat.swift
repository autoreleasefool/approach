import Foundation
import GRDB

enum DatabaseFormat {
	case androidApproach
	case androidBowlingCompanion
	case iOSApproach

	func getImporter() -> Importer? {
		switch self {
		case .iOSApproach:
			IOSApproachSQLiteImporter()
		case .androidApproach, .androidBowlingCompanion:
			nil
		}
	}

	static func of(url: URL) async throws -> DatabaseFormat? {
		let dbQueue = try DatabaseQueue(path: url.path())

		return try await dbQueue.read {
			let iosMigrationsTableExists = try Row.fetchCursor(
				$0,
				sql: "SELECT name FROM sqlite_master WHERE type='table' AND name='grdb_migrations';"
			)

			if try !iosMigrationsTableExists.isEmpty {
				return .iOSApproach
			}

			let approachTableColumns = try Row.fetchCursor(
				$0,
				sql: "PRAGMA table_info(bowlers)"
			)

			while let row = try approachTableColumns.next() {
				let columnName = row["name"] as? String
				if columnName == "name" {
					return .androidApproach
				} else if columnName == "bowler_name" {
					return .androidBowlingCompanion
				}
			}

			return nil
		}
	}
}

import Foundation
import GRDB

enum DatabaseFormat {
	case androidApproach(AndroidApproachVersion)
	case androidBowlingCompanion
	case iOSApproach

	func getImporter() -> DataImporter? {
		switch self {
		case .iOSApproach:
			IOSApproachSQLiteImporter()
		case .androidApproach(.v5):
			AndroidApproachV5SQLiteImporter()
		case .androidApproach(.v1),
				.androidApproach(.v2),
				.androidApproach(.v3),
				.androidApproach(.v4),
				.androidBowlingCompanion:
			StubResultImporter(result: .unrecognized)
		}
	}

	static func of(url: URL) async throws -> DatabaseFormat? {
		let dbQueue = try DatabaseQueue(path: url.path())
		defer { try? dbQueue.close() }

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
					guard let version = try AndroidApproachVersion.extractVersion(from: $0) else { return nil }
					return .androidApproach(version)
				} else if columnName == "bowler_name" {
					return .androidBowlingCompanion
				}
			}

			return nil
		}
	}
}

extension DatabaseFormat {
	enum AndroidApproachVersion: Identifiable, CaseIterable {
		case v1
		case v2
		case v3
		case v4
		case v5

		var id: String {
			switch self {
			case .v1: "d3b407c3b4240e668cd6a72bdebaafa6"
			case .v2: "094b53ae23ac7db98346b3b3aadcfa97"
			case .v3: "552da1a0c2560a7add6cd7cadb8566a1"
			case .v4: "27cb5c5356c1c35c9a3ca31b2e401203"
			case .v5: "ef80e7fe13bb941d36220235f83db3de"
			}
		}

		static func extractVersion(from db: Database) throws -> AndroidApproachVersion? {
			let rows = try Row.fetchCursor(db, sql: "SELECT * FROM room_master_table")

			guard let row = try rows.next() else { return nil }

			guard let identityHash = row["identity_hash"] as? String else { return nil }

			return AndroidApproachVersion.allCases.first { $0.id == identityHash }
		}
	}
}

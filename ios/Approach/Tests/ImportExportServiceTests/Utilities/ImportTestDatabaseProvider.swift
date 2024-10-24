import DatabaseMigrationsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabasePackageLibrary
import GRDBDatabasePackageServiceInterface

@testable import ImportExportService
@testable import ImportExportServiceInterface

final class ImportTestDatabaseProvider {
	let sourceDbUrl: URL
	let sourceDb: GRDBDatabaseProvider

	let destDbUrl: URL
	let destDb: GRDBDatabaseProvider

	// MARK: Initialization

	init(source: DatabaseFormat, dest: DatabaseFormat) throws {
		sourceDbUrl = FileManager.default.temporaryDirectory.appending(path: "\(UUID())")
		destDbUrl = FileManager.default.temporaryDirectory.appending(path: "\(UUID())")

		guard let sourceDb = try Self.openDatabase(format: source, at: sourceDbUrl),
					let destDb = try Self.openDatabase(format: dest, at: destDbUrl) else {
			struct FailedToInit: Error {}
			throw FailedToInit()
		}

		self.sourceDb = sourceDb
		self.destDb = destDb
	}

	deinit {
		try? sourceDb.writer.close()
		try? destDb.writer.close()

		Array(sourceDbUrl.relativeSQLiteFileUrls + destDbUrl.relativeSQLiteFileUrls).forEach {
			try? FileManager.default.removeItem(at: $0)
		}
	}

	// MARK: Private

	private static func openDatabase(format: DatabaseFormat, at url: URL) throws -> GRDBDatabaseProvider? {
		let resourceFile: URL?
		let migrations: [Migration.Type]

		switch format {
		case .androidApproach(.v5):
			resourceFile = Bundle.module.url(
				forResource: "android-approach-v5",
				withExtension: "sqlite"
			)
			migrations = []
		case .androidApproach(.v1),
				.androidApproach(.v2),
				.androidApproach(.v3),
				.androidApproach(.v4),
				.androidBowlingCompanion:
			resourceFile = nil
			migrations = []
		case .iOSApproach:
			resourceFile = url
			migrations = Migrations.approachMigrations
		}

		guard let resourceFile else { return nil }

		if resourceFile != url {
			try FileManager.default.copyItem(at: resourceFile, to: url)
		}

		return GRDBDatabaseProvider(url: url, migrations: migrations)
	}
}

import DatabaseMigrationsLibrary
import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ImportExportServiceInterface
import ModelsLibrary

actor AndroidApproachV5SQLiteImporter: DataImporter {
	func startImport(of: URL, to: URL) throws -> ImportResult {
		let importDbReader: DatabaseReader = try DatabaseQueue(path: of.path())

		// Intialize the database to prepare to import
		let grdb = GRDBDatabaseProvider(url: to, migrations: Migrations.approachMigrations)
		let exportDbWriter = grdb.writer

		// Step-by-step imports of each datatype
		let steps: [SQLiteImportStep] = [
			LocationsImportStep(),
			AlleysImportStep(),
			LanesImportStep(),
			BowlersImportStep(),
			GearImportStep(),
			LeaguesImportStep(),
			SeriesImportStep(),
			GamesImportStep(),
			GameLanesImportStep(),
			GameGearImportStep(),
			FramesImportStep(),
			MatchPlaysImportStep(),
			StatisticsWidgetsImportStep(),
			BowlerPreferredGearImportStep(),
			TeamsImportStep(),
			TeamBowlersImportStep(),
			TeamSeriesImportStep(),
			TeamSeriesSeriesImportStep(),
		]

		// Open importDb for reading and exportDb for writing in unison
		try importDbReader.read { importDb in
			try exportDbWriter.write { exportDb in

				// Import the data types, one by one
				for step in steps {
					try autoreleasepool {
						try step.performImport(from: importDb, to: exportDb)
					}
				}
			}
		}

		return .success
	}
}

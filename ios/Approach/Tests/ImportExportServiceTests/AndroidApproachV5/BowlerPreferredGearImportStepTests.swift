import DatabaseModelsLibrary
import Dependencies
import DependenciesTestSupport
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing
import TestUtilitiesLibrary

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("BowlerPreferredGearImportStep", .tags(.android, .imports, .grdb, .service))
struct BowlerPreferredGearImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.GearImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.BowlerPreferredGearImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let gearId = Gear.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a bowler and gear
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					bowlers(id, name, kind)
				VALUES
					('\(bowlerId)', 'Joseph', 'PLAYABLE');
				"""
			)

			try $0.execute(
				sql: """
				INSERT INTO
					gear(id, name, kind, avatar, owner_id)
				VALUES
					('\(gearId)', 'Yellow', 'BOWLING_BALL', 'Yellow;248,198,164;255,253,53', '\(bowlerId)');
				"""
			)
		}

		try withDependencies {
			$0.uuid = .incrementing
		} operation: {
			try androidDb.read { androidDb in
				try iOSDb.write { iOSdb in
					for preStep in preSteps {
						try preStep.performImport(from: androidDb, to: iOSdb)
					}
				}
			}
		}
	}

	// MARK: Test Properties

	@Test("Imports properties", .tags(.unit))
	func importsProperties() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					bowler_preferred_gear(bowler_id, gear_id)
				VALUES
					('\(bowlerId)', '\(gearId)');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let bowlerPreferredGears = try iOSDb.read {
			try BowlerPreferredGear.Database
				.filter(BowlerPreferredGear.Database.Columns.bowlerId == bowlerId)
				.filter(BowlerPreferredGear.Database.Columns.gearId == gearId)
				.fetchAll($0)
		}

		#expect(bowlerPreferredGears.count == 1)

		guard let bowlerPreferredGear = bowlerPreferredGears.first else {
			struct BowlerPreferredGearNotFound: Error {}
			throw BowlerPreferredGearNotFound()
		}

		#expect(bowlerPreferredGear.bowlerId == bowlerId)
		#expect(bowlerPreferredGear.gearId == gearId)
	}
}

import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing
import TestUtilitiesLibrary

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("LanesImportStep", .tags(.android, .imports, .grdb, .service))
struct LanesImportStepTests {
	let preSteps = [AndroidApproachV5SQLiteImporter.AlleysImportStep()]
	let step = AndroidApproachV5SQLiteImporter.LanesImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let alleyId = Alley.ID()
	let laneId = Lane.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with an alley
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name)
				VALUES
					('\(alleyId)', 'Grandview Lanes');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSdb in
				for preStep in preSteps {
					try preStep.performImport(from: androidDb, to: iOSdb)
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
					lanes(id, alley_id, label, position)
				VALUES
					('\(laneId)', '\(alleyId)', '1', 'LEFT_WALL');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let lane = try iOSDb.read {
			try Lane.Database.fetchOneGuaranteed($0, id: laneId)
		}

		#expect(lane.id == laneId)
		#expect(lane.alleyId == alleyId)
		#expect(lane.label == "1")
		#expect(lane.position == .leftWall)
	}

	@Test(
		"Imports position",
		.tags(.unit),
		arguments: zip(["LEFT_WALL", "RIGHT_WALL", "NO_WALL"], Lane.Position.allCases)
	)
	func importsPosition(from: String, to: Lane.Position) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					lanes(id, alley_id, label, position)
				VALUES
					('\(laneId)', '\(alleyId)', '1', '\(from)');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let lane = try iOSDb.read {
			try Lane.Database.fetchOneGuaranteed($0, id: laneId)
		}

		#expect(lane.id == laneId)
		#expect(lane.position == to)
	}
}

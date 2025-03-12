import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing
import TestUtilitiesLibrary

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("TeamBowlersImportStep", .tags(.android, .imports, .grdb, .service))
struct TeamBowlersImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.TeamsImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.TeamBowlersImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let teamId = Team.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a bowler and a team
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
					teams(id, name)
				VALUES
					('\(teamId)', 'Besties');
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
					team_bowler(team_id, bowler_id, position)
				VALUES
					('\(teamId)', '\(bowlerId)', 123);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let teamBowlers = try iOSDb.read {
			try TeamBowler.Database
				.filter(TeamBowler.Database.Columns.teamId == teamId)
				.filter(TeamBowler.Database.Columns.bowlerId == bowlerId)
				.fetchAll($0)
		}

		#expect(teamBowlers.count == 1)

		guard let teamBowler = teamBowlers.first else {
			struct TeamBowlerNotFound: Error {}
			throw TeamBowlerNotFound()
		}

		#expect(teamBowler.teamId == teamId)
		#expect(teamBowler.bowlerId == bowlerId)
		#expect(teamBowler.position == 123)
	}
}

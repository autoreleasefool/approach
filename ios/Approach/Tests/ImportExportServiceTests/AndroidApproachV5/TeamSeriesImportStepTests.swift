import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing
import TestUtilitiesLibrary

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("TeamSeriesImportStep tests", .tags(.android, .imports))
struct TeamSeriesImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.TeamsImportStep(),
		AndroidApproachV5SQLiteImporter.TeamBowlersImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.TeamSeriesImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let teamId = Team.ID()
	let teamSeriesId = TeamSeries.ID()

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
			try iOSDb.write { iOSdb in
				for preStep in preSteps {
					try preStep.performImport(from: androidDb, to: iOSdb)
				}
			}
		}
	}

	// MARK: Test Properties

	@Test("Imports properties")
	func importsProperties() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					team_series(id, team_id, date, archived_on)
				VALUES
					('\(teamSeriesId)', '\(teamId)', '2024-10-23', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let teamSeries = try iOSDb.read {
			try TeamSeries.Database.fetchOneGuaranteed($0, id: teamSeriesId)
		}

		#expect(teamSeries.id == teamSeriesId)
		#expect(teamSeries.teamId == teamId)
		#expect(teamSeries.date == Date(timeIntervalSince1970: 1_729_666_800))
		#expect(teamSeries.archivedOn == Date(timeIntervalSince1970: 123))
	}
}

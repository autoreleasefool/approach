import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("TeamSeriesSeriesImportStep tests", .tags(.android, .imports))
struct TeamSeriesSeriesImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.LeaguesImportStep(),
		AndroidApproachV5SQLiteImporter.SeriesImportStep(),
		AndroidApproachV5SQLiteImporter.TeamsImportStep(),
		AndroidApproachV5SQLiteImporter.TeamBowlersImportStep(),
		AndroidApproachV5SQLiteImporter.TeamSeriesImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.TeamSeriesSeriesImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let teamId = Team.ID()
	let teamSeriesId = TeamSeries.ID()
	let leagueId = League.ID()
	let seriesId = Series.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a bowler, league, series, and a team
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

			try $0.execute(
				sql: """
				INSERT INTO
					team_series(id, team_id, date)
				VALUES
					('\(teamSeriesId)', '\(teamId)', '2024-10-23');
				"""
			)

			try $0.execute(
				sql: """
				INSERT INTO
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', 'REPEATING', 3, 1000, 4, 'INCLUDE');
				"""
			)

			try $0.execute(
				sql: """
				INSERT INTO
					series(id, league_id, date, applied_date, pre_bowl, exclude_from_statistics)
				VALUES
					('\(seriesId)', '\(leagueId)', '2024-10-22', '2024-10-29', 'REGULAR', 'INCLUDE');
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
					team_series_series(team_series_id, series_id, position)
				VALUES
					('\(teamSeriesId)', '\(seriesId)', 123);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let teamSeriesSeriesList = try iOSDb.read {
			try TeamSeriesSeries.Database
				.filter(TeamSeriesSeries.Database.Columns.seriesId == seriesId)
				.filter(TeamSeriesSeries.Database.Columns.teamSeriesId == teamSeriesId)
				.fetchAll($0)
		}

		#expect(teamSeriesSeriesList.count == 1)

		guard let teamSeriesSeries = teamSeriesSeriesList.first else {
			struct TeamSeriesNotFound: Error {}
			throw TeamSeriesNotFound()
		}

		#expect(teamSeriesSeries.seriesId == seriesId)
		#expect(teamSeriesSeries.teamSeriesId == teamSeriesId)
		#expect(teamSeriesSeries.position == 123)
	}
}

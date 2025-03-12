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

@Suite("SeriesImportStep", .tags(.android, .imports, .grdb, .service))
struct SeriesImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.AlleysImportStep(),
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.LeaguesImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.SeriesImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let alleyId = Alley.ID()
	let bowlerId = Bowler.ID()
	let leagueId = League.ID()
	let seriesId = Series.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with an alley, a bowler and a league
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name)
				VALUES
					('\(alleyId)', 'Grandview Lanes');
				"""
			)

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
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics, archived_on)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', 'REPEATING', 3, 1000, 4, 'INCLUDE', 123000);
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
					series(id, league_id, date, applied_date, pre_bowl, exclude_from_statistics, alley_id, archived_on)
				VALUES
					('\(seriesId)', '\(leagueId)', '2024-10-22', '2024-10-29', 'REGULAR', 'INCLUDE', '\(alleyId)', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let series = try iOSDb.read {
			try Series.Database.fetchOneGuaranteed($0, id: seriesId)
		}

		#expect(series.id == seriesId)
		#expect(series.leagueId == leagueId)
		#expect(series.date == Date(timeIntervalSince1970: 1_729_580_400))
		#expect(series.appliedDate == Date(timeIntervalSince1970: 1_730_185_200))
		#expect(series.preBowl == .regular)
		#expect(series.excludeFromStatistics == .include)
		#expect(series.alleyId == alleyId)
		#expect(series.archivedOn == Date(timeIntervalSince1970: 123))
	}

	@Test(
		"Imports exclude from statistics",
		.tags(.unit),
		arguments: zip(["INCLUDE", "EXCLUDE"], Series.ExcludeFromStatistics.allCases)
	)
	func importsExcludeFromStatistics(from: String, to: Series.ExcludeFromStatistics) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					series(id, league_id, date, applied_date, pre_bowl, exclude_from_statistics)
				VALUES
					('\(seriesId)', '\(leagueId)', '2024-10-22', '2024-10-29', 'REGULAR', '\(from)');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let series = try iOSDb.read {
			try Series.Database.fetchOneGuaranteed($0, id: seriesId)
		}

		#expect(series.excludeFromStatistics == to)
	}

	@Test(
		"Imports applied date",
		.tags(.unit),
		arguments: zip([nil, "2024-10-23"], [nil, Date(timeIntervalSince1970: 1_729_666_800)])
	)
	func importsAppliedDate(from: String?, to: Date?) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					series(id, league_id, date, applied_date, pre_bowl, exclude_from_statistics)
				VALUES
					('\(seriesId)', '\(leagueId)', '2024-10-22', \(from.orNull), 'REGULAR', 'INCLUDE');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let series = try iOSDb.read {
			try Series.Database.fetchOneGuaranteed($0, id: seriesId)
		}

		#expect(series.appliedDate == to)
	}

	@Test(
		"Imports pre bowl",
		.tags(.unit),
		arguments: zip(["REGULAR", "PRE_BOWL"], Series.PreBowl.allCases)
	)
	func importsPreBowl(from: String, to: Series.PreBowl) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					series(id, league_id, date, applied_date, pre_bowl, exclude_from_statistics)
				VALUES
					('\(seriesId)', '\(leagueId)', '2024-10-22', NULL, '\(from)', 'INCLUDE');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let series = try iOSDb.read {
			try Series.Database.fetchOneGuaranteed($0, id: seriesId)
		}

		#expect(series.preBowl == to)
	}
}

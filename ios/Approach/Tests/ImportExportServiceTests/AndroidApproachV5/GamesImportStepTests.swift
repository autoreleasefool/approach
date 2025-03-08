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

@Suite("GamesImportStep tests", .tags(.android, .imports))
struct GamesImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.AlleysImportStep(),
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.LeaguesImportStep(),
		AndroidApproachV5SQLiteImporter.SeriesImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.GamesImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let alleyId = Alley.ID()
	let bowlerId = Bowler.ID()
	let leagueId = League.ID()
	let seriesId = Series.ID()
	let gameId = Game.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with an alley, a bowler, a league, and a series
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
					games(id, series_id, `index`, score, locked, scoring_method, exclude_from_statistics, archived_on, durationMillis)
				VALUES
					('\(gameId)', '\(seriesId)', 1, 225, 'UNLOCKED', 'BY_FRAME', 'INCLUDE', 123000, 456000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let game = try iOSDb.read {
			try Game.Database.fetchOneGuaranteed($0, id: gameId)
		}

		#expect(game.id == gameId)
		#expect(game.seriesId == seriesId)
		#expect(game.index == 1)
		#expect(game.score == 225)
		#expect(game.locked == .open)
		#expect(game.scoringMethod == .byFrame)
		#expect(game.excludeFromStatistics == .include)
		#expect(game.duration == 456)
		#expect(game.archivedOn == Date(timeIntervalSince1970: 123))
	}

	@Test(
		"Imports exclude from statistics",
		arguments: zip(["INCLUDE", "EXCLUDE"], Game.ExcludeFromStatistics.allCases)
	)
	func importsExcludeFromStatistics(from: String, to: Game.ExcludeFromStatistics) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					games(id, series_id, `index`, score, locked, scoring_method, exclude_from_statistics, archived_on, durationMillis)
				VALUES
					('\(gameId)', '\(seriesId)', 1, 225, 'UNLOCKED', 'BY_FRAME', '\(from)', 123000, 456000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let game = try iOSDb.read {
			try Game.Database.fetchOneGuaranteed($0, id: gameId)
		}

		#expect(game.excludeFromStatistics == to)
	}

	@Test(
		"Imports locked",
		arguments: zip(["LOCKED", "UNLOCKED"], Game.Lock.allCases)
	)
	func importsLocked(from: String, to: Game.Lock) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					games(id, series_id, `index`, score, locked, scoring_method, exclude_from_statistics, archived_on, durationMillis)
				VALUES
					('\(gameId)', '\(seriesId)', 1, 225, '\(from)', 'BY_FRAME', 'INCLUDE', 123000, 456000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let game = try iOSDb.read {
			try Game.Database.fetchOneGuaranteed($0, id: gameId)
		}

		#expect(game.locked == to)
	}

	@Test(
		"Imports scoring method",
		arguments: zip(["MANUAL", "BY_FRAME"], Game.ScoringMethod.allCases)
	)
	func importsScoringMethod(from: String, to: Game.ScoringMethod) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					games(id, series_id, `index`, score, locked, scoring_method, exclude_from_statistics, archived_on, durationMillis)
				VALUES
					('\(gameId)', '\(seriesId)', 1, 225, 'UNLOCKED', '\(from)', 'INCLUDE', 123000, 456000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let game = try iOSDb.read {
			try Game.Database.fetchOneGuaranteed($0, id: gameId)
		}

		#expect(game.scoringMethod == to)
	}

	@Test(
		"Imports score",
		arguments: [0, 1, 225, 300, 393, 450]
	)
	func importsScore(score: Int) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					games(id, series_id, `index`, score, locked, scoring_method, exclude_from_statistics, archived_on, durationMillis)
				VALUES
					('\(gameId)', '\(seriesId)', 1, \(score), 'UNLOCKED', 'BY_FRAME', 'INCLUDE', 123000, 456000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let game = try iOSDb.read {
			try Game.Database.fetchOneGuaranteed($0, id: gameId)
		}

		#expect(game.score == score)
	}
}

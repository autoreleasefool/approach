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

@Suite("GameGearImportStep", .tags(.android, .imports, .grdb, .service))
struct GameGearImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.AlleysImportStep(),
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.GearImportStep(),
		AndroidApproachV5SQLiteImporter.LeaguesImportStep(),
		AndroidApproachV5SQLiteImporter.SeriesImportStep(),
		AndroidApproachV5SQLiteImporter.GamesImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.GameGearImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let alleyId = Alley.ID()
	let bowlerId = Bowler.ID()
	let gearId = Gear.ID()
	let leagueId = League.ID()
	let seriesId = Series.ID()
	let gameId = Game.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with an alley, a gear, a bowler, a league, a series, and a game
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
					gear(id, name, kind, avatar, owner_id)
				VALUES
					('\(gearId)', 'Yellow', 'BOWLING_BALL', 'Yellow;248,198,164;255,253,53', '\(bowlerId)');
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

			try $0.execute(
				sql: """
				INSERT INTO
					games(id, series_id, `index`, score, locked, scoring_method, exclude_from_statistics, archived_on, durationMillis)
				VALUES
					('\(gameId)', '\(seriesId)', 1, 225, 'UNLOCKED', 'BY_FRAME', 'INCLUDE', 123000, 456000);
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
					game_gear(game_id, gear_id)
				VALUES
					('\(gameId)', '\(gearId)');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let gameGears = try iOSDb.read {
			try GameGear.Database
				.filter(GameGear.Database.Columns.gameId == gameId)
				.filter(GameGear.Database.Columns.gearId == gearId)
				.fetchAll($0)
		}

		#expect(gameGears.count == 1)

		guard let gameGear = gameGears.first else {
			struct GameGearNotFound: Error {}
			throw GameGearNotFound()
		}

		#expect(gameGear.gameId == gameId)
		#expect(gameGear.gearId == gearId)
	}
}

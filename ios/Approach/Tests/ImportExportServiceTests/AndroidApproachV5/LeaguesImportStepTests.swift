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

@Suite("LeaguesImportStep tests", .tags(.android, .imports))
struct LeaguesImportStepTests {
	let preSteps = [AndroidApproachV5SQLiteImporter.BowlersImportStep()]
	let step = AndroidApproachV5SQLiteImporter.LeaguesImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let leagueId = League.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a bowler
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					bowlers(id, name, kind)
				VALUES
					('\(bowlerId)', 'Joseph', 'PLAYABLE');
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
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics, archived_on)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', 'REPEATING', 3, 1000, 4, 'INCLUDE', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let league = try iOSDb.read {
			try League.Database.fetchOneGuaranteed($0, id: leagueId)
		}

		#expect(league.id == leagueId)
		#expect(league.bowlerId == bowlerId)
		#expect(league.name == "Majors 23-24")
		#expect(league.recurrence == .repeating)
		#expect(league.defaultNumberOfGames == 3)
		#expect(league.additionalPinfall == 1_000)
		#expect(league.additionalGames == 4)
		#expect(league.excludeFromStatistics == .include)
		#expect(league.archivedOn == Date(timeIntervalSince1970: 123))
	}

	@Test("Imports recurrence", arguments: zip(["REPEATING", "ONCE"], League.Recurrence.allCases))
	func importsRecurrence(from: String, to: League.Recurrence) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics, archived_on)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', '\(from)', 3, 1000, 4, 'INCLUDE', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let league = try iOSDb.read {
			try League.Database.fetchOneGuaranteed($0, id: leagueId)
		}

		#expect(league.recurrence == to)
	}

	@Test(
		"Imports exclude from statistics",
		arguments: zip(["INCLUDE", "EXCLUDE"], League.ExcludeFromStatistics.allCases)
	)
	func importsExcludeFromStatistics(from: String, to: League.ExcludeFromStatistics) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics, archived_on)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', 'REPEATING', 3, 1000, 4, '\(from)', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let league = try iOSDb.read {
			try League.Database.fetchOneGuaranteed($0, id: leagueId)
		}

		#expect(league.excludeFromStatistics == to)
	}

	@Test(
		"Imports additional pinfall",
		arguments: [nil, 0, 123, 99_999], [nil, 0, 123, 99_999])
	func importsAdditionalPinfall(additionalPinfall: Int?, additionalGames: Int?) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics, archived_on)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', 'REPEATING', 3, \(additionalPinfall.orNull), \(additionalGames.orNull), 'INCLUDE', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let league = try iOSDb.read {
			try League.Database.fetchOneGuaranteed($0, id: leagueId)
		}

		#expect(league.additionalGames == additionalGames)
		#expect(league.additionalPinfall == additionalPinfall)
	}

	@Test(
		"Imports number of games",
		arguments: [nil, 0, 123, 99_999])
	func importsNumberOfGames(numberOfGames: Int?) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					leagues(id, bowler_id, name, recurrence, number_of_games, additional_pin_fall,
									additional_games, exclude_from_statistics, archived_on)
				VALUES
					('\(leagueId)', '\(bowlerId)', 'Majors 23-24', 'REPEATING', \(numberOfGames.orNull), NULL, NULL, 'INCLUDE', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let league = try iOSDb.read {
			try League.Database.fetchOneGuaranteed($0, id: leagueId)
		}

		#expect(league.defaultNumberOfGames == numberOfGames)
	}
}

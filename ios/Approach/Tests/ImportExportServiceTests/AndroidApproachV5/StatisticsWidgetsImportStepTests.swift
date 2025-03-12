import DatabaseModelsLibrary
import Dependencies
import DependenciesTestSupport
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import StatisticsLibrary
import Testing
import TestUtilitiesLibrary

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("StatisticsWidgetsImportStep", .tags(.android, .imports, .grdb))
struct StatisticsWidgetsImportStepTests {
	let preSteps: [SQLiteImportStep] = [
		AndroidApproachV5SQLiteImporter.BowlersImportStep(),
		AndroidApproachV5SQLiteImporter.LeaguesImportStep(),
	]
	let step = AndroidApproachV5SQLiteImporter.StatisticsWidgetsImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let leagueId = League.ID()
	let widgetId = StatisticsWidget.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a bowler and a league
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

	@Test("Imports properties")
	func importsProperties() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					statistics_widget(id, bowler_id, league_id, timeline, statistic, context, priority)
				VALUES
					('\(widgetId)', '\(bowlerId)', '\(leagueId)', 'THREE_MONTHS', 'ACES', 'overview', 1);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let widget = try iOSDb.read {
			try StatisticsWidget.Database.fetchOneGuaranteed($0, id: widgetId)
		}

		#expect(widget.id == widgetId)
		#expect(widget.bowlerId == bowlerId)
		#expect(widget.leagueId == leagueId)
		#expect(widget.timeline == .past3Months)
		#expect(widget.context == "bowlersList")
		#expect(widget.statistic == Statistics.Aces.title)
		#expect(widget.priority == 1)
	}

	@Test("Imports timeline", arguments: zip(["ONE_MONTH", "THREE_MONTHS", "SIX_MONTHS", "ONE_YEAR", "ALL_TIME"], StatisticsWidget.Timeline.allCases))
	func importsTimeline(from: String, to: StatisticsWidget.Timeline) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					statistics_widget(id, bowler_id, league_id, timeline, statistic, context, priority)
				VALUES
					('\(widgetId)', '\(bowlerId)', '\(leagueId)', '\(from)', 'ACES', 'overview', 1);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let widget = try iOSDb.read {
			try StatisticsWidget.Database.fetchOneGuaranteed($0, id: widgetId)
		}

		#expect(widget.timeline == to)
	}

	@Test(
		"Imports statistic",
		arguments: zip(
			AndroidStatistic.allCases,
			Statistics.allCases
		)
	)
	func importsStatistic(from: AndroidStatistic, to: Statistic.Type) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					statistics_widget(id, bowler_id, league_id, timeline, statistic, context, priority)
				VALUES
					('\(widgetId)', '\(bowlerId)', '\(leagueId)', 'THREE_MONTHS', '\(from)', 'overview', 1);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let widget = try iOSDb.read {
			try StatisticsWidget.Database.fetchOneGuaranteed($0, id: widgetId)
		}

		#expect(widget.statistic == to.title)
	}

	@Test("Imports overview context")
	func importsOverviewContext() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					statistics_widget(id, bowler_id, league_id, timeline, statistic, context, priority)
				VALUES
					('\(widgetId)', '\(bowlerId)', '\(leagueId)', 'THREE_MONTHS', 'ACES', 'overview', 1);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let widget = try iOSDb.read {
			try StatisticsWidget.Database.fetchOneGuaranteed($0, id: widgetId)
		}

		#expect(widget.context == "bowlersList")
	}

	@Test("Imports bowler context")
	func importsBowlerContext() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					statistics_widget(id, bowler_id, league_id, timeline, statistic, context, priority)
				VALUES
					('\(widgetId)', '\(bowlerId)', '\(leagueId)', 'THREE_MONTHS', 'ACES', 'bowler_details_\(bowlerId)', 1);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let widget = try iOSDb.read {
			try StatisticsWidget.Database.fetchOneGuaranteed($0, id: widgetId)
		}

		#expect(widget.context == "leaguesList-\(bowlerId)")
	}
}

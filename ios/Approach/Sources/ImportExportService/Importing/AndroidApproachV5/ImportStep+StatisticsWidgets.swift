import DatabaseModelsLibrary
import Dependencies
import ErrorReportingClientPackageLibrary
import Foundation
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct StatisticsWidgetsImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			let statisticsWidgetRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, bowler_id, league_id, timeline, statistic, context, priority FROM statistics_widget;"
			)

			while let statisticWidgetRow = try statisticsWidgetRows.next() {
				let id: StatisticsWidget.ID = statisticWidgetRow["id"]
				let bowlerId: Bowler.ID? = statisticWidgetRow["bowler_id"]
				let leagueId: League.ID? = statisticWidgetRow["league_id"]
				let androidTimeline: String = statisticWidgetRow["timeline"]
				let androidStatistic: String = statisticWidgetRow["statistic"]
				let androidContext: String = statisticWidgetRow["context"]
				let priority: Int = statisticWidgetRow["priority"]

				guard let statistic = AndroidStatistic(rawValue: androidStatistic)?.statistic else {
					@Dependency(\.errors) var errors
					errors.captureMessage("Unable to parse statistic '\(androidStatistic)' while importing AndroidApproachV5")
					continue
				}

				guard statistic.supportsWidgets else {
					@Dependency(\.errors) var errors
					errors.captureMessage("Statistic '\(statistic.title)' does not support widgets while importing AndroidApproachV5")
					continue
				}

				guard let context = parseContext(androidContext, bowlerId: bowlerId),
							let timeline = parseTimeline(androidTimeline) else {
					continue
				}

				let statisticWidget = StatisticsWidget.Database(
					id: id,
					created: .now,
					bowlerId: bowlerId,
					leagueId: leagueId,
					timeline: timeline,
					statistic: statistic.title,
					context: context,
					priority: priority
				)

				try statisticWidget.insert(exportDb)
			}
		}

		private func parseContext(_ context: String, bowlerId: Bowler.ID?) -> String? {
			if context == "overview" {
				"bowlersList"
			} else if context.starts(with: "bowler_details"), let bowlerId {
				"leaguesList-\(bowlerId)"
			} else {
				nil
			}
		}

		private func parseTimeline(_ timeline: String) -> StatisticsWidget.Timeline? {
			switch timeline {
			case "ONE_MONTH": .past1Month
			case "THREE_MONTHS": .past3Months
			case "SIX_MONTHS": .past6Months
			case "ONE_YEAR": .pastYear
			case "ALL_TIME": .allTime
			default: nil
			}
		}
	}
}

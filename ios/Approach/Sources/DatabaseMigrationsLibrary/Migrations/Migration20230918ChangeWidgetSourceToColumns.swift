import Foundation
import GRDB
import GRDBDatabasePackageLibrary

private enum StatisticsWidgetLegacySource: Codable, DatabaseValueConvertible {
	case bowler(UUID)
	case league(UUID)
}

// swiftlint:disable:next type_name
struct Migration20230918ChangeWidgetSourceToColumns: Migration {
	static func migrate(_ db: Database) throws {
		try db.alter(table: "statisticsWidget") { t in
			t.add(column: "bowlerId", .text)
				.indexed()
				.references("bowler", onDelete: .cascade)

			t.add(column: "leagueId", .text)
				.indexed()
				.references("league", onDelete: .cascade)
		}

		let widgets = try Row.fetchAll(db, sql: "SELECT id, source FROM statisticsWidget")
		for widget in widgets {
			guard let widgetId = UUID.fromDatabaseValue(widget["id"]),
						let source = StatisticsWidgetLegacySource.fromDatabaseValue(widget["source"]) else {
				continue
			}

			switch source {
			case let .bowler(bowlerId):
				try db.execute(sql: "UPDATE statisticsWidget SET bowlerId=? WHERE id=?", arguments: [bowlerId, widgetId])
			case let .league(leagueId):
				try db.execute(sql: "UPDATE statisticsWidget SET leagueId=? WHERE id=?", arguments: [leagueId, widgetId])
			}
		}

		try db.execute(sql: """
			DELETE FROM statisticsWidget
			WHERE bowlerId NOT IN (SELECT DISTINCT bowler.id from bowler) AND bowlerId IS NOT NULL
		""")

		try db.execute(sql: """
			DELETE FROM statisticsWidget
			WHERE leagueId NOT IN (SELECT DISTINCT league.id from league) AND leagueId IS NOT NULL
		""")

		try db.alter(table: "statisticsWidget") { t in
			t.drop(column: "source")
		}
	}
}

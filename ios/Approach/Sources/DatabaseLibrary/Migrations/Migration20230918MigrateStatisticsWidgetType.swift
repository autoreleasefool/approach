import GRDB

// swiftlint:disable:next type_name
struct Migration20230918MigrateStatisticsWidgetType: DBMigration {
	static func migrate(_ db: Database) throws {
		let migrations: [(previous: String, updated: String)] = [
			("average", "Average"),
			("middleHits", "Middle Hits"),
			("averagePinsLeftOnDeck", "Average Pins Left on Deck"),
		]

		for migration in migrations {
			try db.execute(
				sql: "UPDATE statisticsWidget SET statistic=? WHERE statistic=?",
				arguments: [migration.updated, migration.previous]
			)
		}
	}
}

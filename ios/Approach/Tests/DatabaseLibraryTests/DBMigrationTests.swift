@testable import DatabaseLibrary
import GRDB
import XCTest

final class DBMigrationTests: XCTestCase {
	static let allMigrations = [
		"Migration20230325CreateBowler",
		"Migration20230408CreateAlley",
		"Migration20230408CreateLeague",
		"Migration20230409CreateSeries",
		"Migration20230413CreateGear",
		"Migration20230414CreateGame",
		"Migration20230414CreateFrame",
		"Migration20230415CreateLane",
		"Migration20230417CreateSeriesLanePivot",
		"Migration20230425CreateAvatar",
		"Migration20230426CreateLocation",
		"Migration20230506AddGearToFrame",
	]

	func testIdentifier() {
		XCTAssertEqual(
			Migration20230325CreateBowler.identifier,
			"Migration20230325CreateBowler"
		)
	}

	func testRegistersMigration() throws {
		var migrator = DatabaseMigrator()
		XCTAssertEqual(migrator.migrations, [])

		migrator.registerMigration(Migration20230325CreateBowler.self)
		XCTAssertEqual(migrator.migrations, ["Migration20230325CreateBowler"])
	}

	func testRegistersAllMigrations() throws {
		var migrator = DatabaseMigrator()
		migrator.registerDBMigrations()

		XCTAssertEqual(migrator.migrations, Self.allMigrations)
	}

	func testRunsMigrations() throws {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()

		let appliedBefore = try dbQueue.read {
			try migrator.appliedMigrations($0)
		}
		XCTAssertEqual(appliedBefore, [])

		migrator.registerDBMigrations()
		try migrator.migrate(dbQueue)

		let appliedAfter = try dbQueue.read {
			try migrator.appliedMigrations($0)
		}
		XCTAssertEqual(appliedAfter, Self.allMigrations)
	}
}
